package n7.ad2.games.internal.party

import android.os.SystemClock
import kotlinx.coroutines.CoroutineScope
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
import n7.ad2.games.room.GameMode
import n7.ad2.games.room.MAX_PLAYERS
import n7.ad2.games.room.Player
import n7.ad2.games.room.Room
import n7.ad2.games.room.RoomFactory
import n7.ad2.games.room.RoomMessage
import n7.ad2.games.room.RoomState
import java.net.InetAddress

/**
 * Игра по сети до [MAX_PLAYERS] игроков без лобби у каждой игры.
 * Пока виден список игр: держим свою комнату, объявляем ее в Wi-Fi (NSD) и по Wi-Fi Direct и ищем других.
 * «Сыграть» - входим в комнату выбранного игрока. Вошли к нам - своя комната стала сессией, мы хост и ждем еще игроков,
 * пока есть места. Хост выбирает игру и режим, гости жмут «Готов», хост запускает; комната переживает игры.
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
    private val clock: () -> Long = SystemClock::elapsedRealtime,
) {

    private companion object {
        // сервер слушает все интерфейсы: и Wi-Fi сеть, и группу Wi-Fi Direct
        val ANY_ADDRESS: InetAddress = InetAddress.getByAddress(byteArrayOf(0, 0, 0, 0))
        const val LOBBY_CHANNEL = "lobby"
    }

    private val _state = MutableStateFlow(PartyState())
    val state: StateFlow<PartyState> = _state.asStateFlow()
    private val _openGame = MutableSharedFlow<RoomState.Playing>(extraBufferCapacity = 1)
    val openGame: SharedFlow<RoomState.Playing> = _openGame.asSharedFlow()

    /** Своя комната: пока один - чтобы нас нашли, с гостями - сессия, где мы хост */
    private var hostRoom: Room? = null
    private var hostPort: Int? = null

    /** Комната другого игрока, в которую входим или вошли */
    private var joinRoom: Room? = null
    private var joinVia: Via? = null
    private var joinTargetId: String? = null

    /** Комната сессии: [hostRoom] с гостями или [joinRoom], когда мы в ней */
    private var sessionRoom: Room? = null
    private var lobby = Lobby()
    private var openedSeed: Long? = null
    private val roomJobs = mutableMapOf<Room, Job>()
    private var discoveryJob: Job? = null
    private var joinJob: Job? = null
    private var visible = false
    private var deviceIp = ""
    private var directPermission = false
    private var nsdRegistered = false
    private var directAdvertised = false

    private val isHost: Boolean get() = sessionRoom != null && sessionRoom === hostRoom

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

    /** Список игр (или комната) на экране: объявляемся и ищем; ушли с экрана - перестаем (сессия остается) */
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
        _state.update { it.copy(connecting = true, failure = null) }
        joinJob = scope.launch {
            val address = when (player.via) {
                Via.DIRECT -> player.deviceAddress?.let { wifiDirect.connect(it) }.also { directAdvertised = false }
                Via.WIFI -> withContext(Dispatchers.IO) { runCatching { InetAddress.getByName(player.ip) }.getOrNull() }
            }
            if (address == null) {
                _state.update { it.copy(connecting = false, failure = PartyFailure.CONNECTION) }
                return@launch
            }
            val room = roomFactory.join(me, address, player.port)
            joinRoom = room
            joinVia = player.via
            joinTargetId = player.id
            leaderboardSync.attach(room)
            observe(room)
        }
    }

    /** @param address "ip:port" */
    fun joinManual(address: String) {
        val ip = address.substringBeforeLast(":").trim()
        val port = address.substringAfterLast(":").trim().toIntOrNull() ?: return
        if (ip.isNotEmpty()) play(NearbyPlayer(id = address, name = address, Via.WIFI, ip, port, deviceAddress = null))
    }

    /** Хост выбирает игру комнаты: к новой игре все снова жмут «Готов» */
    fun pick(gameId: String, mode: GameMode) {
        if (!isHost) return
        lobby = lobby.pick(gameId, mode)
        publishLobby()
    }

    /** Гость готов или нет к игре, которую выбрал хост */
    fun setReady(ready: Boolean) {
        val room = sessionRoom ?: return
        if (isHost) return
        lobby = lobby.ready(me.id, ready)
        publishSession()
        runCatching { room.sendSync(LOBBY_CHANNEL, LobbyCodec.encodeReady(ready)) }
    }

    /** Хост запускает выбранную игру, когда готовы все */
    fun start() {
        val room = sessionRoom ?: return
        val ready = room.state.value as? RoomState.Ready ?: return
        val gameId = lobby.gameId ?: return
        if (isHost && lobby.canStart(ready.players)) runCatching { room.startGame(gameId, lobby.mode) }
    }

    /** «Ещё раз» с экрана итогов: хост выбирает ту же игру, гость готов к ней */
    fun rematch(pick: Pick) {
        if (isHost) pick(pick.gameId, pick.mode) else setReady(true)
    }

    /** Выйти из комнаты; хост закрывает ее у всех */
    fun leave() {
        val room = sessionRoom ?: return
        leaveRoom(room)
        if (room === hostRoom) {
            hostRoom = null
            hostPort = null
        }
        if (room === joinRoom) joinRoom = null
        endSession()
    }

    /** Экран игр закрыт насовсем */
    fun release() {
        listOfNotNull(hostRoom, joinRoom).forEach(::leaveRoom)
        hostRoom = null
        joinRoom = null
        sessionRoom = null
        unregister()
        wifiDirect.release()
        ActiveRoom.clear()
    }

    private fun startPresence() {
        // гость в чужой комнате себя не объявляет
        if (sessionRoom != null && !isHost) return
        if (hostRoom == null) host()
        advertise()
        if (sessionRoom == null) startDiscovery()
    }

    private fun stopPresence() {
        discoveryJob?.cancel()
        _state.update { it.copy(nearby = emptyList()) }
        // в игре к нам не войти (идет игра), а вернемся - объявимся снова
        unregister()
        if (sessionRoom != null) return
        joinJob?.cancel()
        listOfNotNull(hostRoom, joinRoom).forEach(::leaveRoom)
        hostRoom = null
        hostPort = null
        joinRoom = null
        if (directAdvertised) wifiDirect.release()
        directAdvertised = false
        _state.update { it.copy(connecting = false, myAddress = null, wifiDirect = false) }
    }

    private fun host() {
        val room = roomFactory.host(me, ANY_ADDRESS)
        hostRoom = room
        leaderboardSync.attach(room)
        observe(room)
    }

    // sync слушаем сразу: лобби от хоста приходит следом за составом, раньше, чем мы успеем принять комнату
    private fun observe(room: Room) {
        roomJobs[room] = scope.launch {
            launch { room.syncEvents.collect { message -> onSync(room, message) } }
            room.state.collect { state -> onRoomState(room, state) }
        }
    }

    private fun onRoomState(room: Room, state: RoomState) {
        when (state) {
            RoomState.Connecting -> Unit
            is RoomState.Waiting -> if (room === hostRoom) {
                hostPort = state.port
                // все гости ушли: снова просто ждем
                if (room === sessionRoom) endSession()
                updateAddress()
                advertise()
            }
            is RoomState.Ready -> when {
                room === sessionRoom -> onSessionReady(state.players)
                room === joinRoom -> onJoined(room, state.players)
                room === hostRoom -> onGuestsCame(room)
            }
            is RoomState.Playing -> if (room === sessionRoom) onPlaying(room, state)
            is RoomState.Closed -> onClosed(room, state)
        }
    }

    /** К нам вошли. Если мы сами входим к игроку с большим id, ждем: он оставит свою комнату, и мы войдем к нему */
    private fun onGuestsCame(room: Room) {
        val joining = joinRoom?.state?.value?.let { it !is RoomState.Closed } == true
        if (joining && joinTargetId.orEmpty() > me.id) return
        joinJob?.cancel()
        joinRoom?.let(::leaveRoom)
        joinRoom = null
        sessionRoom = room
        openedSeed = null
        lobby = Lobby()
        discoveryJob?.cancel()
        _state.update { it.copy(nearby = emptyList(), connecting = false, failure = null) }
        val ready = room.state.value as? RoomState.Ready ?: return
        onSessionReady(ready.players)
    }

    /** Мы вошли в чужую комнату. Если в нашу уже вошли, а наш id больше, остаемся у себя */
    private fun onJoined(room: Room, players: List<Player>) {
        val myGuests = sessionRoom === hostRoom && hostRoom != null
        if (myGuests && players.first().id < me.id) {
            leaveRoom(room)
            joinRoom = null
            return
        }
        // своя комната больше не нужна: ее гости вернутся к списку и смогут войти сюда сами
        hostRoom?.let(::leaveRoom)
        hostRoom = null
        hostPort = null
        sessionRoom = room
        openedSeed = null
        discoveryJob?.cancel()
        unregister()
        _state.update { it.copy(nearby = emptyList(), connecting = false, failure = null, myAddress = null) }
        onSessionReady(players)
    }

    /** Состав поменялся или игра кончилась: хост рассылает лобби, в том числе новичкам */
    private fun onSessionReady(players: List<Player>) {
        val room = sessionRoom ?: return
        openedSeed = null
        if (isHost) {
            lobby = lobby.forPlayers(players)
            publishLobby()
            advertise()
        } else {
            publishSession()
        }
    }

    private fun onPlaying(room: Room, playing: RoomState.Playing) {
        // во время игры Playing меняется, когда кто-то уходит: игра уже открыта
        if (playing.seed != openedSeed) {
            openedSeed = playing.seed
            ActiveRoom.set(room, playing.mode, clock())
            _openGame.tryEmit(playing)
            // к следующей игре каждый снова жмет «Готов»: кто доиграл раньше и нажал «Ещё раз», так и останется готов
            if (isHost) lobby = lobby.reset()
        }
        if (isHost) publishLobby() else publishSession()
    }

    private fun onClosed(room: Room, closed: RoomState.Closed) {
        roomJobs.remove(room)?.cancel()
        when {
            room === sessionRoom -> {
                if (room === joinRoom) joinRoom = null
                if (room === hostRoom) {
                    hostRoom = null
                    hostPort = null
                }
                endSession()
            }
            room === joinRoom -> {
                joinRoom = null
                _state.update { it.copy(connecting = false, failure = PartyFailure.of(closed.reason)) }
                // пока входили, к нам вошли сами
                val own = hostRoom
                if (own != null) {
                    leaderboardSync.attach(own)
                    if (own.state.value is RoomState.Ready) onGuestsCame(own)
                }
            }
            room === hostRoom -> {
                hostRoom = null
                hostPort = null
                if (visible && sessionRoom == null) host()
            }
        }
    }

    private fun endSession() {
        sessionRoom = null
        lobby = Lobby()
        openedSeed = null
        ActiveRoom.clear()
        // группа Wi-Fi Direct сессии больше не нужна; своя появится заново при объявлении
        if (hostRoom == null) {
            wifiDirect.release()
            directAdvertised = false
        }
        _state.update { it.copy(session = null, wifiDirect = false) }
        if (visible) startPresence()
    }

    private fun onSync(room: Room, message: RoomMessage.Sync) {
        if (message.channel != LOBBY_CHANNEL) return
        when (val decoded = LobbyCodec.decode(message.payload)) {
            is LobbyCodec.Message.State -> if (room !== hostRoom) {
                lobby = decoded.lobby
                if (room === sessionRoom) publishSession()
            }
            is LobbyCodec.Message.Ready -> if (room === hostRoom && isHost) {
                lobby = lobby.ready(message.playerId, decoded.ready)
                publishLobby()
            }
            null -> Unit
        }
    }

    /** Хост: показать лобби и разослать всем */
    private fun publishLobby() {
        publishSession()
        val room = sessionRoom ?: return
        runCatching { room.sendSync(LOBBY_CHANNEL, LobbyCodec.encode(lobby)) }
    }

    private fun publishSession() {
        val room = sessionRoom ?: return
        val players = when (val state = room.state.value) {
            is RoomState.Ready -> state.players
            is RoomState.Playing -> state.players
            else -> return
        }
        val via = if (room === joinRoom) joinVia else null
        _state.update { it.copy(session = Session(players, me.id, lobby, via)) }
    }

    // объявляемся, пока в своей комнате есть места: Wi-Fi и разрешение могут появиться позже комнаты
    private fun advertise() {
        if (!visible || (sessionRoom != null && !isHost)) return
        val room = hostRoom ?: return
        val port = hostPort ?: return
        val players = (room.state.value as? RoomState.Ready)?.players?.size ?: 1
        if (players >= MAX_PLAYERS) return unregister()
        if (!nsdRegistered && deviceIp.isNotEmpty()) {
            nsdRegistered = true
            val server = SimpleServer(PartyCodec.presenceName(me.name, me.id), deviceIp, port)
            scope.launch(Dispatchers.IO) { runCatching { registerServiceInNetworkUseCase.register(server) } }
        }
        if (!directAdvertised && directPermission) {
            directAdvertised = true
            scope.launch {
                val visibleNearby = wifiDirect.advertise(me.name, port, me.id)
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
        val port = hostPort
        _state.update { it.copy(myAddress = if (deviceIp.isNotEmpty() && port != null) "$deviceIp:$port" else null) }
    }

    private fun leaveRoom(room: Room) {
        roomJobs.remove(room)?.cancel()
        room.leave()
    }
}
