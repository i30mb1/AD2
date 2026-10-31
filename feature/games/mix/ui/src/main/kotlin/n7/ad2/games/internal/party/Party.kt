package n7.ad2.games.internal.party

import android.os.SystemClock
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
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
import n7.ad2.games.room.Player
import n7.ad2.games.room.Room
import n7.ad2.games.room.RoomFactory
import n7.ad2.games.room.RoomState
import java.net.InetAddress

/**
 * Игра вдвоем без лобби у каждой игры.
 * Пока виден список игр: держим свою комнату, объявляем ее в Wi-Fi (NSD) и по Wi-Fi Direct и ищем других.
 * «Сыграть» - входим в комнату выбранного игрока; кто первым стал Ready - та комната и есть сессия, она переживает игры.
 * В сессии оба выбирают игру и режим, одинаковый выбор - хост запускает игру.
 * Все вызовы и изменения - на главном потоке ([scope] = viewModelScope).
 */
internal class Party(
    private val me: Player,
    private val scope: CoroutineScope,
    private val roomFactory: RoomFactory,
    private val leaderboardSync: LeaderboardSync,
    private val getNetworkStateUseCase: GetNetworkStateUseCase,
    private val discoverServicesInNetworkUseCase: DiscoverServicesInNetworkUseCase,
    private val registerServiceInNetworkUseCase: RegisterServiceInNetworkUseCase,
    private val wifiDirect: WifiDirect,
) {

    private companion object {
        // сервер слушает все интерфейсы: и Wi-Fi сеть, и группу Wi-Fi Direct
        val ANY_ADDRESS: InetAddress = InetAddress.getByAddress(byteArrayOf(0, 0, 0, 0))

        // оба нажали «Сыграть» одновременно: ждем, не станет ли Ready комната предпочтительного хоста
        const val DOUBLE_JOIN_GRACE_MS = 1_500L
        const val PARTY_CHANNEL = "party"
    }

    private val _state = MutableStateFlow(PartyState())
    val state: StateFlow<PartyState> = _state.asStateFlow()
    private val _openGame = MutableSharedFlow<RoomState.Playing>(extraBufferCapacity = 1)
    val openGame: SharedFlow<RoomState.Playing> = _openGame.asSharedFlow()

    private var picks = PartyPicks()
    private var presenceRoom: Room? = null
    private var joinRoom: Room? = null
    private var joinVia: Via? = null
    private var sessionRoom: Room? = null
    private val roomJobs = mutableMapOf<Room, Job>()
    private var syncJob: Job? = null
    private var discoveryJob: Job? = null
    private var joinJob: Job? = null
    private var graceJob: Job? = null
    private var visible = false
    private var deviceIp = ""
    private var directPermission = false
    private var nsdRegistered = false
    private var directAdvertised = false

    init {
        scope.launch {
            getNetworkStateUseCase()
                .flowOn(Dispatchers.IO)
                .catch { }
                .collect { network ->
                    deviceIp = (network as? NetworkState.Connected)?.ip.orEmpty()
                    updateAddress()
                    advertise()
                }
        }
    }

    /** Список игр на экране: объявляемся и ищем; ушли с экрана - перестаем (сессия остается) */
    fun setVisible(visible: Boolean) {
        if (this.visible == visible) return
        this.visible = visible
        if (visible) startPresence() else stopPresence()
    }

    fun onWifiDirectPermission(granted: Boolean) {
        if (!granted || directPermission) return
        directPermission = true
        advertise()
        if (visible && sessionRoom == null) startDiscovery()
    }

    fun play(player: NearbyPlayer) {
        if (sessionRoom != null || joinJob?.isActive == true) return
        joinRoom?.let(::leaveRoom)
        joinRoom = null
        _state.update { it.copy(connecting = true, failed = false) }
        joinJob = scope.launch {
            val address = when (player.via) {
                Via.DIRECT -> player.deviceAddress?.let { wifiDirect.connect(it) }.also { directAdvertised = false }
                Via.WIFI -> withContext(Dispatchers.IO) { runCatching { InetAddress.getByName(player.ip) }.getOrNull() }
            }
            if (address == null) {
                _state.update { it.copy(connecting = false, failed = true) }
                return@launch
            }
            val room = roomFactory.join(me, address, player.port)
            joinRoom = room
            joinVia = player.via
            observe(room)
        }
    }

    /** @param address "ip:port" */
    fun joinManual(address: String) {
        val ip = address.substringBeforeLast(":").trim()
        val port = address.substringAfterLast(":").trim().toIntOrNull() ?: return
        if (ip.isNotEmpty()) play(NearbyPlayer(id = address, name = address, Via.WIFI, ip, port, deviceAddress = null))
    }

    fun pick(pick: Pick) {
        val room = sessionRoom ?: return
        picks = picks.pick(pick)
        publishPicks()
        runCatching { room.sendSync(PARTY_CHANNEL, PartyCodec.encodePick(pick)) }
        startIfMatched()
    }

    fun cancelPick() {
        val room = sessionRoom ?: return
        picks = picks.cancel()
        publishPicks()
        runCatching { room.sendSync(PARTY_CHANNEL, PartyCodec.encodePick(null)) }
    }

    fun leave() {
        val room = sessionRoom ?: return
        roomJobs.remove(room)?.cancel()
        room.leave()
        endSession()
    }

    /** Экран игр закрыт насовсем */
    fun release() {
        (listOfNotNull(presenceRoom, joinRoom, sessionRoom)).forEach(::leaveRoom)
        presenceRoom = null
        joinRoom = null
        sessionRoom = null
        unregister()
        wifiDirect.release()
        ActiveRoom.clear()
    }

    private fun startPresence() {
        if (sessionRoom != null) return
        if (presenceRoom == null) host()
        advertise()
        startDiscovery()
    }

    private fun stopPresence() {
        discoveryJob?.cancel()
        _state.update { it.copy(nearby = emptyList()) }
        if (sessionRoom != null) return
        joinJob?.cancel()
        listOfNotNull(presenceRoom, joinRoom).forEach(::leaveRoom)
        presenceRoom = null
        joinRoom = null
        unregister()
        if (directAdvertised) wifiDirect.release()
        directAdvertised = false
        _state.update { it.copy(connecting = false, myAddress = null, wifiDirect = false) }
    }

    private fun host() {
        val room = roomFactory.host(me, ANY_ADDRESS)
        presenceRoom = room
        observe(room)
    }

    private fun observe(room: Room) {
        roomJobs[room] = scope.launch { room.state.collect { state -> onRoomState(room, state) } }
    }

    private fun onRoomState(room: Room, state: RoomState) {
        when (state) {
            RoomState.Connecting -> Unit
            is RoomState.Waiting -> if (room === presenceRoom) {
                updateAddress()
                advertise()
            }
            is RoomState.Ready -> if (room !== sessionRoom) onReady(room, state)
            is RoomState.Playing -> if (room === sessionRoom) {
                picks = PartyPicks()
                publishPicks()
                ActiveRoom.set(room, state.mode, SystemClock.elapsedRealtime())
                _openGame.tryEmit(state)
            }
            is RoomState.Closed -> onClosed(room)
        }
    }

    private fun onReady(room: Room, ready: RoomState.Ready) {
        val peer = if (ready.host.id == me.id) ready.guest else ready.host
        val otherAttempt = listOfNotNull(presenceRoom, joinRoom, sessionRoom).any { it !== room && it.state.value !is RoomState.Closed && it.state.value !is RoomState.Waiting }
        if (isPreferredHost(ready.host.id, me.id, peer.id) || !otherAttempt) {
            accept(room, peer)
        } else {
            graceJob?.cancel()
            graceJob = scope.launch {
                delay(DOUBLE_JOIN_GRACE_MS)
                if (sessionRoom == null && room.state.value is RoomState.Ready) accept(room, peer)
            }
        }
    }

    private fun accept(room: Room, peer: Player) {
        graceJob?.cancel()
        val via = if (room === joinRoom) joinVia else null
        listOfNotNull(presenceRoom, joinRoom, sessionRoom).filter { it !== room }.forEach(::leaveRoom)
        presenceRoom = null
        joinRoom = null
        sessionRoom = room
        discoveryJob?.cancel()
        unregister()
        leaderboardSync.attach(room)
        picks = PartyPicks()
        syncJob?.cancel()
        syncJob = scope.launch {
            room.syncEvents.collect { message ->
                if (message.channel != PARTY_CHANNEL) return@collect
                val picked = PartyCodec.decodePick(message.payload) ?: return@collect
                picks = picks.peerPicked(picked.pick)
                publishPicks()
                startIfMatched()
            }
        }
        _state.update { it.copy(session = Session(peer, via), nearby = emptyList(), connecting = false, failed = false, myPick = null, peerPick = null) }
    }

    private fun onClosed(room: Room) {
        roomJobs.remove(room)?.cancel()
        when {
            room === sessionRoom -> endSession()
            room === joinRoom -> {
                joinRoom = null
                _state.update { it.copy(connecting = false, failed = true) }
            }
            room === presenceRoom -> {
                presenceRoom = null
                if (visible && sessionRoom == null) host()
            }
        }
    }

    private fun endSession() {
        sessionRoom = null
        syncJob?.cancel()
        picks = PartyPicks()
        ActiveRoom.clear()
        // группа Wi-Fi Direct сессии больше не нужна; своя появится заново при объявлении
        wifiDirect.release()
        directAdvertised = false
        _state.update { it.copy(session = null, myPick = null, peerPick = null, wifiDirect = false) }
        if (visible) startPresence()
    }

    // хост запускает игру, гость ждет StartGame; во время чужой игры (Playing) старт отложится до возврата в Ready
    private fun startIfMatched() {
        val match = picks.match ?: return
        val room = sessionRoom ?: return
        val ready = room.state.value as? RoomState.Ready ?: return
        if (ready.host.id == me.id) runCatching { room.startGame(match.gameId, match.mode) }
    }

    private fun publishPicks() = _state.update { it.copy(myPick = picks.mine, peerPick = picks.peer) }

    // объявляемся, как только есть порт и сеть: Wi-Fi и разрешение могут появиться позже комнаты
    private fun advertise() {
        if (!visible || sessionRoom != null) return
        val waiting = presenceRoom?.state?.value as? RoomState.Waiting ?: return
        if (!nsdRegistered && deviceIp.isNotEmpty()) {
            nsdRegistered = true
            val server = SimpleServer(PartyCodec.presenceName(me.name, me.id), deviceIp, waiting.port)
            scope.launch(Dispatchers.IO) { runCatching { registerServiceInNetworkUseCase.register(server) } }
        }
        if (!directAdvertised && directPermission) {
            directAdvertised = true
            scope.launch {
                val visibleNearby = wifiDirect.advertise(me.name, waiting.port, me.id)
                _state.update { it.copy(wifiDirect = visibleNearby) }
            }
        }
    }

    private fun unregister() {
        if (!nsdRegistered) return
        nsdRegistered = false
        runCatching { registerServiceInNetworkUseCase.unregister() }
    }

    private fun startDiscovery() {
        discoveryJob?.cancel()
        val lan = discoverServicesInNetworkUseCase()
            .map { servers -> servers.map { LanPresence(it.name, it.ip, it.port) } }
            .catch { emit(emptyList()) }
            .onStart { emit(emptyList()) }
            .flowOn(Dispatchers.IO)
        val direct = if (directPermission) {
            wifiDirect.discover().catch { emit(emptyList()) }.onStart { emit(emptyList()) }
        } else {
            flowOf(emptyList())
        }
        discoveryJob = scope.launch {
            combine(lan, direct) { inWifi, nearby -> mergeNearby(inWifi, nearby, me.id) }
                .collect { players -> _state.update { it.copy(nearby = players) } }
        }
    }

    private fun updateAddress() {
        val port = (presenceRoom?.state?.value as? RoomState.Waiting)?.port
        _state.update { it.copy(myAddress = if (deviceIp.isNotEmpty() && port != null) "$deviceIp:$port" else null) }
    }

    private fun leaveRoom(room: Room) {
        roomJobs.remove(room)?.cancel()
        room.leave()
    }
}
