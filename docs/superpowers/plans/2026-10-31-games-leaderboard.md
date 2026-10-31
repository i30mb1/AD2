# Games Leaderboard (Plan 3 of 4) Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Local leaderboard per phone (Room DB) that merges with the other player's records whenever a room is open, and pushes new records live.

**Architecture:** `feature/games/room` gets a generic `Sync(channel, payload)` message usable in `Ready` and `Playing`. New `feature/games/leaderboard/api` (JVM: `Record`, `LeaderboardRepository`, `LocalPlayerProvider`) and `feature/games/leaderboard/impl` (Android: Room database, repository, `LeaderboardSync` that binds a repository to a `Room` over channel `"records"`, `LocalPlayerStore`).

**Tech Stack:** Room 2.7 (KSP), kotlinx.serialization, coroutines, JUnit4 + Truth + Robolectric.

**Spec:** `docs/superpowers/specs/2026-10-31-games-tab-design.md` — section "Рекорды".

## Global Constraints

- Best result per (player, game, mode); tie → the earlier `achievedAt` wins (spec: "максимум по паре игрок+игра", mode added because Solo/Versus/Coop scores are not comparable).
- Records of met players stay after disconnect (spec).
- Player identity = random UUID stored locally + device name (spec "Решения за builder'ом").
- Build/test from shell as in Plan 2; `gradlew --stop` before a build if `classes.jar` is locked.

## Review Focus

- Records sync while a game is already `Playing` (not only in `Ready`) — Task 1 allows `Sync` in both; Task 3 test `submit while Playing broadcasts`.
- Peer sends a record for *my* player id with a lower score (stale copy): must not overwrite my best — Task 2 `merge keeps the better local record`.
- Garbage payload on channel `"records"`: ignored, no crash — Task 3.
- Same score, different time: earlier wins on both phones, so both tables converge — Task 2 tie test.
- Many records (>125 bytes payload): relies on Plan 2 frame fix; Task 3 test sends 50 records through the fake room.

---

### Task 1: Generic Sync channel in the room

**Files:** Modify `feature/games/room/api/.../RoomMessage.kt`, `Room.kt`; `feature/games/room/impl/.../RoomEngine.kt`. Tests: `RoomCodecTest`, `RoomEngineTest`.

**Interfaces — Produces:**
- `@SerialName("sync") data class RoomMessage.Sync(val channel: String, val payload: String)`
- `Room.syncEvents: Flow<RoomMessage.Sync>` (only from the other player), `Room.sendSync(channel: String, payload: String)` — allowed in `Ready` and `Playing`, else `IllegalStateException`.

- [ ] Tests first: codec round-trip includes `Sync`; engine `sync is delivered in Ready and Playing`, `sendSync in Waiting throws and sends nothing`. Run, watch fail, implement, run green, commit `games room: sync channel`.

### Task 2: Records storage

**Files:** Create modules `feature/games/leaderboard/api` (`convention.kotlin-jvm`, `convention.kotlin-serialization`; `api(projects.feature.games.room.api)`) and `feature/games/leaderboard/impl` (`convention.android-library`, `convention.kotlin-serialization`, `n7.plugins.kotlin-ksp`; Room runtime/ktx + `ksp(libs.room.compiler)`; tests junit, truth, robolectric, `libs.test.core`, coroutines-test).

**Interfaces — Produces (`n7.ad2.games.leaderboard`):**
```kotlin
@Serializable data class Record(val playerId: String, val playerName: String, val gameId: String, val mode: GameMode, val score: Int, val achievedAt: Long)
fun Record.isBetterThan(other: Record): Boolean   // score higher, or equal score and earlier achievedAt
interface LeaderboardRepository {
    fun observe(gameId: String, mode: GameMode): Flow<List<Record>>   // score desc, achievedAt asc
    suspend fun all(): List<Record>
    suspend fun submit(record: Record): Boolean                         // true if it became the stored best
    suspend fun merge(records: List<Record>): List<Record>              // returns only the records that improved the table
}
interface LocalPlayerProvider { fun localPlayer(): Player }
```
Impl: `internal class RoomLeaderboardRepository(dao: RecordDao) : LeaderboardRepository`, `RecordEntity` with primary key (playerId, gameId, mode), `LeaderboardDatabase`; public `fun LeaderboardRepository(context: Context): LeaderboardRepository` (file DB `games_leaderboard.db`).

- [ ] Robolectric tests on an in-memory DB (`LeaderboardRepositoryTest`): `submit stores first record`, `worse score is ignored`, `better score replaces`, `tie keeps earlier achievedAt`, `merge returns only improvements`, `merge keeps the better local record`, `observe sorts by score then time and filters by game and mode`. Fail → implement → green → commit `games leaderboard: storage`.

### Task 3: LeaderboardSync and local player

**Files:** `feature/games/leaderboard/impl/.../LeaderboardSync.kt`, `LocalPlayerStore.kt`; tests `LeaderboardSyncTest` (fake `Room` + repository from Task 2 on in-memory DB), `LocalPlayerStoreTest`.

**Interfaces — Produces:**
- `class LeaderboardSync(private val repository: LeaderboardRepository, private val scope: CoroutineScope)` with `fun attach(room: Room)` and `suspend fun submit(record: Record)`.
  - On the room entering `Ready` (once per attach): `sendSync("records", json(all()))`.
  - On `Sync` with channel `"records"`: decode `List<Record>` (ignore garbage), `repository.merge(...)`.
  - `submit`: `repository.submit`; if it improved and the attached room is `Ready`/`Playing`, `sendSync("records", json(listOf(record)))`.
- `class LocalPlayerStore(context: Context) : LocalPlayerProvider` — id `UUID` in SharedPreferences `games_player`, name `Build.MODEL`; same id across instances.

- [ ] Tests: `sends all records when room becomes Ready`, `merges records received from peer`, `garbage payload is ignored`, `submit while Playing broadcasts`, `submit when room closed only stores`, `50 records round-trip through sync`; `LocalPlayerStore keeps the same id`. Fail → implement → green → commit `games leaderboard: sync and local player`; push.
