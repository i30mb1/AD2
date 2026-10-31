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
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import n7.ad2.feature.games.xo.domain.DiscoverServicesInNetworkUseCase
import n7.ad2.feature.games.xo.domain.GetNetworkStateUseCase
import n7.ad2.feature.games.xo.domain.RegisterServiceInNetworkUseCase
import n7.ad2.feature.games.xo.domain.WifiDirect
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
 * Лобби сетевой игры: вкладка «Создать» поднимает комнату и объявляет ее в Wi-Fi сети (NSD) и рядом по Wi-Fi Direct,
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
    private val wifiDirect: WifiDirect,
    localPlayerProvider: LocalPlayerProvider,
) : ViewModel() {

    private companion object {
        // сервер слушает все интерфейсы: и Wi-Fi сеть, и группу Wi-Fi Direct
        val ANY_ADDRESS: InetAddress = InetAddress.getByAddress(byteArrayOf(0, 0, 0, 0))
    }

    @AssistedFactory
    interface Factory {
        fun create(mode: GameMode, gameId: String): LobbyViewModel
    }

    private val _state = MutableStateFlow(
        State(Tab.CREATE, localPlayerProvider.localPlayer(), deviceIp = "", wifiDirect = false, directVisible = false, direct = DirectStatus.IDLE, roomState = null, isHost = true, servers = emptyList()),
    )
    val state: StateFlow<State> = _state.asStateFlow()
    // игра и режим берутся у хоста: гость мог зайти в лобби другой игры
    private val _openGame = MutableSharedFlow<RoomState.Playing>(extraBufferCapacity = 1)
    val openGame: SharedFlow<RoomState.Playing> = _openGame.asSharedFlow()

    private var room: Room? = null
    private var roomJob: Job? = null
    private var discoveryJob: Job? = null
    private var directJob: Job? = null
    private var handedOver = false
    private var nsdRegistered = false
    private var directUsed = false

    // все изменения room/roomJob - на главном потоке: сеть шлет статус несколько раз, и без этого поднимались две комнаты
    init {
        host()
        viewModelScope.launch {
            getNetworkStateUseCase()
                .flowOn(Dispatchers.IO)
                .catch { }
                .collect { network ->
                    val ip = (network as? NetworkState.Connected)?.ip.orEmpty()
                    _state.update { it.copy(deviceIp = ip) }
                    advertise()
                }
        }
    }

    /** Разрешение на поиск устройств рядом: без него Wi-Fi Direct не работает */
    fun onWifiDirectPermission(granted: Boolean) {
        if (!granted || _state.value.wifiDirect) return
        _state.update { it.copy(wifiDirect = true) }
        when (_state.value.tab) {
            Tab.CREATE -> advertise()
            Tab.FIND -> startDiscovery()
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

    fun join(game: FoundGame) {
        val deviceAddress = game.deviceAddress
        if (deviceAddress == null) connect(game.ip, game.port) else connectDirect(deviceAddress, game.port)
    }

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
        if (room != null) return
        // сам сервер стартует на IO внутри фабрики
        observe(roomFactory.host(_state.value.me, ANY_ADDRESS), isHost = true)
    }

    private fun connect(ip: String, port: Int) {
        leaveRoom()
        viewModelScope.launch {
            val address = withContext(Dispatchers.IO) { runCatching { InetAddress.getByName(ip) }.getOrNull() } ?: return@launch
            if (room == null) observe(roomFactory.join(_state.value.me, address, port), isHost = false)
        }
    }

    private fun connectDirect(deviceAddress: String, port: Int) {
        leaveRoom()
        directUsed = true
        _state.update { it.copy(direct = DirectStatus.CONNECTING) }
        directJob = viewModelScope.launch {
            val host = wifiDirect.connect(deviceAddress)
            _state.update { it.copy(direct = if (host == null) DirectStatus.FAILED else DirectStatus.IDLE) }
            if (host != null && room == null) observe(roomFactory.join(_state.value.me, host, port), isHost = false)
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
                    is RoomState.Waiting -> advertise()
                    is RoomState.Playing -> {
                        unregister()
                        handedOver = true
                        // группа Wi-Fi Direct нужна до конца игры, закроется вместе с комнатой
                        ActiveRoom.set(room, roomState.mode, onClose = if (directUsed) wifiDirect::release else ({}))
                        _openGame.tryEmit(roomState)
                    }
                    else -> if (isHost) unregister()
                }
            }
        }
    }

    // объявляем комнату, как только есть порт и сеть: Wi-Fi и разрешение могут появиться позже комнаты
    private fun advertise() {
        val state = _state.value
        val waiting = state.roomState as? RoomState.Waiting ?: return
        if (!nsdRegistered && state.deviceIp.isNotEmpty()) {
            nsdRegistered = true
            viewModelScope.launch(Dispatchers.IO) {
                runCatching { registerServiceInNetworkUseCase.register(SimpleServer(waiting.host.name, state.deviceIp, waiting.port)) }
            }
        }
        if (!directUsed && state.wifiDirect) {
            directUsed = true
            directJob = viewModelScope.launch {
                val visible = wifiDirect.advertise(waiting.host.name, waiting.port)
                _state.update { it.copy(directVisible = visible) }
            }
        }
    }

    private fun startDiscovery() {
        discoveryJob?.cancel()
        val wifi = discoverServicesInNetworkUseCase()
            .map { servers -> servers.filter { it.ip != _state.value.deviceIp }.map { FoundGame(it.name, it.ip, it.port) } }
            .catch { emit(emptyList()) }
            .onStart { emit(emptyList()) }
        val direct = if (_state.value.wifiDirect) {
            wifiDirect.discover()
                .map { rooms -> rooms.map { FoundGame(it.name, ip = "", it.port, it.deviceAddress) } }
                .catch { emit(emptyList()) }
                .onStart { emit(emptyList()) }
        } else {
            flowOf(emptyList())
        }
        discoveryJob = viewModelScope.launch {
            combine(wifi.flowOn(Dispatchers.IO), direct) { inWifi, nearby -> inWifi + nearby }
                .collect { games -> _state.update { it.copy(servers = games) } }
        }
    }

    private fun leaveRoom() {
        roomJob?.cancel()
        directJob?.cancel()
        unregister()
        room?.leave()
        room = null
        if (directUsed) wifiDirect.release()
        directUsed = false
        _state.update { it.copy(roomState = null, directVisible = false, direct = DirectStatus.IDLE) }
    }

    private fun unregister() {
        if (!nsdRegistered) return
        nsdRegistered = false
        runCatching { registerServiceInNetworkUseCase.unregister() }
    }

    override fun onCleared() {
        if (!handedOver) leaveRoom() else unregister()
    }

    enum class Tab { CREATE, FIND }

    enum class DirectStatus { IDLE, CONNECTING, FAILED }

    /** @param deviceAddress не null - комната найдена по Wi-Fi Direct, ip станет известен после входа в группу */
    @Immutable
    data class FoundGame(val name: String, val ip: String, val port: Int, val deviceAddress: String? = null)

    @Immutable
    data class State(
        val tab: Tab,
        val me: Player,
        val deviceIp: String,
        val wifiDirect: Boolean,
        val directVisible: Boolean,
        val direct: DirectStatus,
        val roomState: RoomState?,
        val isHost: Boolean,
        val servers: List<FoundGame>,
    )
}
