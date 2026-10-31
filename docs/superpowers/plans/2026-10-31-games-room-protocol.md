# Games Room Protocol (Plan 2 of 4) Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** A game-agnostic two-player "room" over the existing websocket transport: one phone hosts, the other joins, the host starts a game with a shared seed, and both exchange game events.

**Architecture:** New modules `feature/games/room/api` (pure JVM: protocol messages, codec, `Room`/`RoomFactory` interfaces) and `feature/games/room/impl` (Android lib: `RoomEngine` state machine over an internal `Transport`, websocket adapters over the existing `WebsocketServerController`/`WebsocketClientController` from `feature/games/xo/domain/impl`). The transport code stays where it is; it is already game-agnostic. Wi-Fi Direct needs no extra code here: once devices are in a P2P group they have IPs and the same room works over them.

**Tech Stack:** Kotlin, coroutines/Flow, kotlinx.serialization (JSON, plugin `convention.kotlin-serialization`), JUnit4 + Truth + kotlinx-coroutines-test/debug.

**Spec:** `docs/superpowers/specs/2026-10-31-games-tab-design.md` (sections "Комната", step 2 of "Порядок").

## Global Constraints

- No central server; host = one of the phones (spec).
- Rooms are exactly two players: host + one guest (spec: "вдвоём по сети"; the server controller accepts one client).
- Dagger only in `wiring` modules (CLAUDE.md). This plan adds no wiring: `RoomFactory` is consumed by Plan 4, which wires it.
- Do not change `SocketServerController`/`HttpServerController` and their clients; only the websocket pair is touched.
- Build/test from the shell: `$env:JAVA_HOME = "C:\Program Files\Android\Android Studio1\jbr"; .\gradlew.bat <task>` (Studio's gradle tool timed out in this project; quiet logging: success prints nothing, check test XML in `build/test-results`).
- New modules are picked up by the custom include plugin from `projects.*` dependencies; verify with `:app:assembleDebug` only once something in `:app`'s graph depends on them (Plan 4). In this plan run module tasks directly, e.g. `:feature:games:room:impl:testDebugUnitTest`.

## Review Focus

- A websocket text frame longer than 125 bytes (any `Welcome` with long names, later leaderboard sync): must arrive intact both directions — covered by Task 1 tests (126, 300, 70 000 bytes).
- Host leaves mid-game: guest must end in `Closed(PEER_LEFT)`, not hang in `Playing` — Task 2 test `host transport closes while Playing`.
- Guest with a different `PROTOCOL_VERSION`: guest gets `Closed(VERSION_MISMATCH)`, host stays `Waiting` — Task 2.
- Garbage / unknown message type on the wire (older or newer app): ignored, room keeps working — Task 1 codec test + Task 2 engine test.
- `startGame` or `sendEvent` called in the wrong state (guest calls `startGame`, anyone sends before `Playing`): `IllegalStateException`, nothing is sent — Task 2.

---

### Task 1: Websocket frames longer than 125 bytes

**Files:**
- Create: `feature/games/xo/domain/impl/src/main/kotlin/n7/ad2/feature/games/xo/domain/internal/server/WebSocketFrames.kt`
- Modify: `.../internal/server/controller/WebsocketServerController.kt` (its `sendWebSocketFrame` and frame reading) and `.../controller/WebsocketClientController.kt` (`sendWebSocketFrame`, `readWebSocketFrame`) to delegate to `WebSocketFrames`
- Test: `feature/games/xo/domain/impl/src/test/kotlin/WebSocketFramesTest.kt`

**Interfaces:**
- Produces (internal, same module):
  - `internal object WebSocketFrames`
  - `fun writeText(output: OutputStream, text: String, mask: Boolean)` — FIN+text opcode; payload length 0..125 inline, 126..65535 as `126` + 2-byte big-endian, larger as `127` + 8-byte big-endian; when `mask` is true sets the MASK bit, writes a random 4-byte key and XORs the payload (client → server per RFC 6455).
  - `fun read(input: InputStream): FrameType` — returns `FrameType.Text(String)` or `FrameType.Close`; extended lengths read as unsigned (`and 0xFF` on each byte); unmasks when the MASK bit is set; end of stream (`read() == -1`) → `FrameType.Close`.

- [ ] **Step 1: Write the failing tests** — round-trip through `ByteArrayOutputStream` → `ByteArrayInputStream`:
  - `text of N bytes survives masked and unmasked` parameterized over N = 0, 1, 125, 126, 300, 65535, 70000 (use ASCII `"a".repeat(N)`), both `mask = true/false`; assert `read(...) == FrameType.Text(original)`.
  - `cyrillic text survives` — `"привет".repeat(30)` (>125 bytes in UTF-8), masked.
  - `length 126 frame header is 0x7E followed by two bytes` — write 126 bytes unmasked, assert bytes[1] == 126 and bytes[2..3] == 0x00, 0x7E.
  - `empty stream reads as Close`.
- [ ] **Step 2: Run** `.\gradlew.bat :feature:games:xo:domain:impl:testDebugUnitTest --tests WebSocketFramesTest` — Expected: compile failure (`WebSocketFrames` unresolved).
- [ ] **Step 3: Implement `WebSocketFrames`**, then replace the controllers' private frame functions with calls to it (server writes `mask = false`, client writes `mask = true`). Keep `FrameType` as is.
- [ ] **Step 4: Run all module tests** `.\gradlew.bat :feature:games:xo:domain:impl:testDebugUnitTest` — Expected: `WebSocketFramesTest` and the existing 13 tests pass.
- [ ] **Step 5: Commit** `fix xo: websocket frames longer than 125 bytes`.

### Task 2: Room protocol and engine

**Files:**
- Create module `feature/games/room/api/build.gradle.kts`: plugins `convention.kotlin-jvm`, `convention.kotlin-serialization`; deps `api(libs.coroutines)`; tests `libs.test.junit`, `libs.test.truth.jvm`.
- Create `feature/games/room/api/src/main/kotlin/n7/ad2/games/room/RoomMessage.kt`, `RoomCodec.kt`, `Room.kt`
- Create module `feature/games/room/impl/build.gradle.kts`: plugins `convention.android-library`; deps `api(projects.feature.games.room.api)`, `implementation(projects.feature.games.xo.domain.impl)`, `implementation(libs.coroutines)`; tests `libs.test.junit`, `libs.test.truth.jvm`, `libs.test.coroutines`, `libs.test.coroutines.debug`.
- Create `feature/games/room/impl/src/main/kotlin/n7/ad2/games/room/internal/Transport.kt`, `RoomEngine.kt`
- Test: `feature/games/room/api/src/test/kotlin/RoomCodecTest.kt`, `feature/games/room/impl/src/test/kotlin/RoomEngineTest.kt` (with a `FakeTransport` in the test source set)

**Interfaces:**
- Produces in `n7.ad2.games.room` (api):
  ```kotlin
  const val PROTOCOL_VERSION = 1
  @Serializable data class Player(val id: String, val name: String)
  @Serializable enum class GameMode { SOLO, VERSUS, COOP }
  @Serializable sealed interface RoomMessage {
      @Serializable @SerialName("hello") data class Hello(val player: Player, val protocolVersion: Int) : RoomMessage
      @Serializable @SerialName("welcome") data class Welcome(val host: Player, val guest: Player) : RoomMessage
      @Serializable @SerialName("reject") data class Reject(val reason: String) : RoomMessage
      @Serializable @SerialName("start") data class StartGame(val gameId: String, val mode: GameMode, val seed: Long) : RoomMessage
      @Serializable @SerialName("event") data class GameEvent(val playerId: String, val payload: String) : RoomMessage
      @Serializable @SerialName("leave") data class Leave(val playerId: String) : RoomMessage
  }
  object RoomCodec { fun encode(message: RoomMessage): String; fun decode(text: String): RoomMessage? }
  sealed interface RoomState {
      data object Connecting : RoomState
      data class Waiting(val host: Player, val port: Int) : RoomState
      data class Ready(val host: Player, val guest: Player) : RoomState
      data class Playing(val host: Player, val guest: Player, val gameId: String, val mode: GameMode, val seed: Long) : RoomState
      data class Closed(val reason: CloseReason) : RoomState
  }
  enum class CloseReason { LEFT, PEER_LEFT, CONNECTION_FAILED, VERSION_MISMATCH }
  interface Room {
      val me: Player
      val state: StateFlow<RoomState>
      val events: Flow<RoomMessage.GameEvent>   // only the other player's events
      fun startGame(gameId: String, mode: GameMode)   // host in Ready only
      fun sendEvent(payload: String)                  // Playing only
      fun leave()
  }
  interface RoomFactory {
      fun host(me: Player, bindAddress: InetAddress): Room
      fun join(me: Player, host: InetAddress, port: Int): Room
  }
  ```
- Produces in `n7.ad2.games.room.internal` (impl):
  ```kotlin
  internal sealed interface TransportStatus {
      data object Connecting : TransportStatus
      data class Listening(val port: Int) : TransportStatus
      data object Connected : TransportStatus
      data class Closed(val failed: Boolean) : TransportStatus
  }
  internal interface Transport {
      val status: StateFlow<TransportStatus>
      val incoming: Flow<String>
      fun send(text: String)
      fun close()
  }
  internal class RoomEngine(override val me: Player, private val isHost: Boolean, private val transport: Transport, scope: CoroutineScope, private val seedSource: () -> Long = { Random.nextLong() }) : Room
  ```

`RoomCodec` uses `Json { ignoreUnknownKeys = true; classDiscriminator = "type" }`; `decode` returns `null` for malformed JSON or an unknown `type`.

Engine rules (the part the tests pin; implementer writes the reducer):

| Who | Trigger | Effect |
|---|---|---|
| host | transport `Listening(port)` | state `Waiting(me, port)` |
| host | `Hello` with `protocolVersion != PROTOCOL_VERSION` | send `Reject("version")`, stay `Waiting` |
| host | `Hello` ok | send `Welcome(me, guest)`, state `Ready(me, guest)` |
| host | `startGame(id, mode)` in `Ready` | send `StartGame(id, mode, seedSource())`, state `Playing` with the same seed |
| guest | transport `Connected` | send `Hello(me, PROTOCOL_VERSION)` |
| guest | `Welcome` | state `Ready(host, guest)` |
| guest | `Reject` | state `Closed(VERSION_MISMATCH)`, close transport |
| guest | `StartGame` | state `Playing(...)` with received seed |
| both | `GameEvent` from the other player | emitted on `events` |
| both | `sendEvent(p)` in `Playing` | send `GameEvent(me.id, p)` |
| both | `Leave` received, or transport `Closed` after `Ready`/`Playing` | state `Closed(PEER_LEFT)` |
| guest | transport `Closed(failed = true)` before `Ready` | state `Closed(CONNECTION_FAILED)` |
| both | `leave()` | send `Leave(me.id)`, close transport, state `Closed(LEFT)` |
| both | `decode` returns null | ignored |

- [ ] **Step 1: Write the failing codec tests** in `RoomCodecTest`:
  - `every message round-trips` — each `RoomMessage` subtype: `decode(encode(m)) == m`.
  - `type discriminator is on the wire` — `encode(Leave("p1"))` contains `"type":"leave"`.
  - `unknown fields are ignored` — `decode("""{"type":"leave","playerId":"p1","extra":1}""") == Leave("p1")`.
  - `unknown type and garbage decode to null` — `"""{"type":"chat","text":"x"}"""` and `"not json"` → `null`.
- [ ] **Step 2: Write the failing engine tests** in `RoomEngineTest` (`runTest` with `StandardTestDispatcher`, `FakeTransport` exposes `MutableStateFlow<TransportStatus>`, a `MutableSharedFlow<String>` for incoming, and records `sent: List<RoomMessage>` decoded via `RoomCodec`; host seed `{ 42L }`):
  - `host waits then accepts guest` — Listening(5000) → `Waiting(host, 5000)`; incoming `Hello(guest, 1)` → sent `Welcome(host, guest)`, state `Ready(host, guest)`.
  - `host rejects other protocol version` — incoming `Hello(guest, 2)` → sent `Reject("version")`, state still `Waiting`.
  - `host start game sends seed` — from Ready, `startGame("spell", VERSUS)` → sent `StartGame("spell", VERSUS, 42)`, state `Playing(host, guest, "spell", VERSUS, 42)`.
  - `guest says hello on connect and follows host` — status Connected → sent `Hello(guest, 1)`; incoming Welcome → `Ready`; incoming `StartGame("spell", COOP, 7)` → `Playing(..., seed = 7)`.
  - `guest gets VERSION_MISMATCH on reject` — incoming `Reject("version")` → `Closed(VERSION_MISMATCH)` and `transport.closed == true`.
  - `events from the other player are emitted, own are not` — in Playing, incoming `GameEvent("guest", "a")` → `events` emits it; `sendEvent("b")` → sent `GameEvent("host", "b")`.
  - `host transport closes while Playing` → guest/host state `Closed(PEER_LEFT)`.
  - `connect failure before Ready` — guest, status `Closed(failed = true)` → `Closed(CONNECTION_FAILED)`.
  - `leave sends Leave and closes` → sent `Leave(me.id)`, `Closed(LEFT)`.
  - `wrong-state calls throw and send nothing` — guest `startGame` in Ready, host `sendEvent` in Waiting → `IllegalStateException`, `sent` unchanged.
  - `garbage on the wire is ignored` — incoming `"not json"` in Ready → state unchanged.
- [ ] **Step 3: Run** `.\gradlew.bat :feature:games:room:api:test` and `.\gradlew.bat :feature:games:room:impl:testDebugUnitTest` — Expected: compile failures (types missing).
- [ ] **Step 4: Implement** `RoomMessage.kt`, `RoomCodec.kt`, `Room.kt` (api) and `Transport.kt`, `RoomEngine.kt` (impl) per the Interfaces block and the table.
- [ ] **Step 5: Run both commands again** — Expected: all codec and engine tests pass.
- [ ] **Step 6: Commit** `games room: protocol, codec and engine`.

### Task 3: Websocket transport and RoomFactory

**Files:**
- Create: `feature/games/room/impl/src/main/kotlin/n7/ad2/games/room/internal/WebsocketHostTransport.kt`, `WebsocketGuestTransport.kt`
- Create: `feature/games/room/impl/src/main/kotlin/n7/ad2/games/room/WebsocketRoomFactory.kt` (public)
- Test: `feature/games/room/impl/src/test/kotlin/WebsocketRoomTest.kt`

**Interfaces:**
- Consumes: `WebsocketServerController(scope)` / `WebsocketClientController(scope)` and their `state` (`ServerState`, `ClientState`, `Message.Other`) from `feature/games/xo/domain/impl`; `Transport`, `RoomEngine` from Task 2.
- Produces: `class WebsocketRoomFactory(private val scope: CoroutineScope) : RoomFactory` — `host` starts a `WebsocketServerController` on `bindAddress`, port `0`; `join` connects a `WebsocketClientController`. Each returns a `RoomEngine` over the matching transport.

Adapter rules the implementer cannot infer:
- The controllers start in `ServerStatus.Closed` / `ClientStatus.Disconnected` before `start`/`connect`; the adapter reports `TransportStatus.Connecting` until it has seen a non-closed status, and only then maps `Closed`/`Disconnected` to `TransportStatus.Closed`.
- Host: `Waiting(server)` → `Listening(server.port)`, `Connected` → `Connected`, later `Closed` → `Closed(failed = false)`.
- Guest: `Connected` → `Connected`; a `Message.Info` starting with `"Connect failed"` while still connecting → `Closed(failed = true)`; `Disconnected` after `Connected` → `Closed(failed = false)`.
- `incoming` emits the text of every new `Message.Other` in `state.messages`; track the last seen index and reset it to 0 when the list shrinks (controllers clear messages on disconnect).

- [ ] **Step 1: Write the failing loopback tests** (`runBlocking`, `CoroutinesTimeout.seconds(10)`, factory on `CoroutineScope(Job() + Dispatchers.IO)`, cancel in `finally`):
  - `guest joins host and both reach Ready` — `host(Player("h","Host"), loopback)`; await `Waiting`, take `port`; `join(Player("g","Guest"), loopback, port)`; both states become `Ready(Player("h","Host"), Player("g","Guest"))`.
  - `start game reaches guest with the same seed and events flow both ways` — host `startGame("spell", VERSUS)`; both `Playing` with equal `seed`; host `sendEvent("x".repeat(500))` arrives on guest `events` intact; guest `sendEvent("y")` arrives on host.
  - `host leaves and guest sees PEER_LEFT`.
  - `join to a closed port ends CONNECTION_FAILED`.
- [ ] **Step 2: Run** `.\gradlew.bat :feature:games:room:impl:testDebugUnitTest --tests WebsocketRoomTest` — Expected: compile failure.
- [ ] **Step 3: Implement** both transports and `WebsocketRoomFactory`.
- [ ] **Step 4: Run all room tests** `.\gradlew.bat :feature:games:room:impl:testDebugUnitTest` and `.\gradlew.bat :feature:games:room:api:test` — Expected: all pass.
- [ ] **Step 5: Commit** `games room: websocket transport and factory`.

## Self-Review

- Spec "Комната" (host hands out identical rounds, collects answers, broadcasts state): identical rounds = shared `seed` in `StartGame`; answers/state = `GameEvent` payloads, whose game-specific format belongs to Plan 4. Covered by Tasks 2–3.
- Spec "Сетевой слой выносится из xo в общий модуль игр": the reusable part is the new `room` module; the transport classes stay in `feature/games/xo/domain` because they are already game-agnostic and moving ~40 files is pure churn. Renaming that module to `games/network` can be a standalone refactor later if wanted.
- Spec "Рекорды" (sync on connect) is Plan 3; it will add message types to `RoomMessage` (unknown types are already ignored, so older peers keep working).
- Player id persistence (spec: device name + random id stored locally) is Plan 3; here `Player` is a parameter.
