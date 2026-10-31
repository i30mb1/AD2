package n7.ad2.games.internal.lobby

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import n7.ad2.feature.games.xo.domain.DiscoverServicesInNetworkUseCase
import n7.ad2.feature.games.xo.domain.GetNetworkStateUseCase
import n7.ad2.feature.games.xo.domain.RegisterServiceInNetworkUseCase
import n7.ad2.feature.games.xo.domain.model.NetworkState
import n7.ad2.feature.games.xo.domain.model.SimpleServer
import n7.ad2.games.internal.ActiveRoom
import n7.ad2.games.leaderboard.LeaderboardSync
import n7.ad2.games.leaderboard.LocalPlayerProvider
import n7.ad2.games.room.GameMode
import n7.ad2.games.room.Player
import n7.ad2.games.room.Room
import n7.ad2.games.room.RoomFactory
import n7.ad2.games.room.RoomState
import java.net.InetAddress

/**
 * Лобби сетевой игры: вкладка «Создать» поднимает комнату и регистрирует ее в сети (NSD),
 * вкладка «Найти» показывает найденные комнаты и позволяет подключиться по ip:port
 */
internal class LobbyViewModel @AssistedInject constructor(
    @Assisted val mode: GameMode,
    @Assisted private val gameId: String,
    private val roomFactory: RoomFactory,
    private val leaderboardSync: LeaderboardSync,
    private val getNetworkStateUseCase: GetNetworkStateUseCase,
    private val discoverServicesInNetworkUseCase: DiscoverServicesInNetworkUseCase,
    private val registerServiceInNetworkUseCase: RegisterServiceInNetworkUseCase,
    localPlayerProvider: LocalPlayerProvider,
) : ViewModel() {

    @AssistedFactory
    interface Factory {
        fun create(mode: GameMode, gameId: String): LobbyViewModel
    }

    private val _state = MutableStateFlow(State(Tab.CREATE, localPlayerProvider.localPlayer(), deviceIp = "", roomState = null, isHost = true, servers = emptyList()))
    val state: StateFlow<State> = _state.asStateFlow()
    // игра и режим берутся у хоста: гость мог зайти в лобби другой игры
    private val _openGame = MutableSharedFlow<RoomState.Playing>(extraBufferCapacity = 1)
    val openGame: SharedFlow<RoomState.Playing> = _openGame.asSharedFlow()

    private var room: Room? = null
    private var roomJob: Job? = null
    private var discoveryJob: Job? = null
    private var handedOver = false

    // все изменения room/roomJob - на главном потоке: сеть шлет статус несколько раз, и без этого поднимались две комнаты
    init {
        viewModelScope.launch {
            getNetworkStateUseCase()
                .flowOn(Dispatchers.IO)
                .catch { }
                .collect { network ->
                    val ip = (network as? NetworkState.Connected)?.ip.orEmpty()
                    _state.update { it.copy(deviceIp = ip) }
                    if (ip.isNotEmpty() && room == null && _state.value.tab == Tab.CREATE) host()
                }
        }
    }

    fun selectTab(tab: Tab) {
        if (tab == _state.value.tab) return
        leaveRoom()
        _state.update { it.copy(tab = tab, servers = emptyList()) }
        when (tab) {
            Tab.CREATE -> {
                discoveryJob?.cancel()
                host()
            }
            Tab.FIND -> startDiscovery()
        }
    }

    fun recreate() {
        leaveRoom()
        host()
    }

    fun join(game: FoundGame) = connect(game.ip, game.port)

    /** @param address "ip:port" */
    fun joinManual(address: String) {
        val ip = address.substringBeforeLast(":").trim()
        val port = address.substringAfterLast(":").trim().toIntOrNull() ?: return
        if (ip.isNotEmpty()) connect(ip, port)
    }

    fun start() {
        runCatching { room?.startGame(gameId, mode) }
    }

    private fun host() {
        val ip = _state.value.deviceIp
        if (ip.isEmpty() || room != null) return
        // ip - литерал, getByName не ходит в DNS; сам сервер стартует на IO внутри фабрики
        observe(roomFactory.host(_state.value.me, InetAddress.getByName(ip)), isHost = true)
    }

    private fun connect(ip: String, port: Int) {
        leaveRoom()
        viewModelScope.launch {
            val address = withContext(Dispatchers.IO) { runCatching { InetAddress.getByName(ip) }.getOrNull() } ?: return@launch
            if (room == null) observe(roomFactory.join(_state.value.me, address, port), isHost = false)
        }
    }

    private fun observe(room: Room, isHost: Boolean) {
        this.room = room
        leaderboardSync.attach(room)
        _state.update { it.copy(isHost = isHost, roomState = room.state.value) }
        roomJob = viewModelScope.launch {
            room.state.collect { roomState ->
                _state.update { it.copy(roomState = roomState) }
                when (roomState) {
                    is RoomState.Waiting -> withContext(Dispatchers.IO) {
                        runCatching { registerServiceInNetworkUseCase.register(SimpleServer(roomState.host.name, _state.value.deviceIp, roomState.port)) }
                    }
                    is RoomState.Playing -> {
                        unregister()
                        handedOver = true
                        ActiveRoom.set(room, roomState.mode)
                        _openGame.tryEmit(roomState)
                    }
                    else -> if (isHost) unregister()
                }
            }
        }
    }

    private fun startDiscovery() {
        discoveryJob?.cancel()
        discoveryJob = viewModelScope.launch(Dispatchers.IO) {
            discoverServicesInNetworkUseCase()
                .catch { }
                .collect { servers ->
                    val games = servers.filter { it.ip != _state.value.deviceIp }.map { FoundGame(it.name, it.ip, it.port) }
                    _state.update { it.copy(servers = games) }
                }
        }
    }

    private fun leaveRoom() {
        roomJob?.cancel()
        unregister()
        room?.leave()
        room = null
        _state.update { it.copy(roomState = null) }
    }

    private fun unregister() {
        runCatching { registerServiceInNetworkUseCase.unregister() }
    }

    override fun onCleared() {
        if (!handedOver) leaveRoom() else unregister()
    }

    enum class Tab { CREATE, FIND }

    @Immutable
    data class FoundGame(val name: String, val ip: String, val port: Int)

    @Immutable
    data class State(
        val tab: Tab,
        val me: Player,
        val deviceIp: String,
        val roomState: RoomState?,
        val isHost: Boolean,
        val servers: List<FoundGame>,
    )
}
