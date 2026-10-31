# Games Modes UI (Plan 4 of 4, part 1: Угадай спелл) Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:executing-plans. Steps use checkbox (`- [ ]`) syntax.

**Goal:** "Угадай спелл" playable in Solo / Versus / Coop with lobby, live leaderboard and record badges, per the approved mockups `docs/superpowers/mockups/2026-10-31-games-modes.html`.

**Architecture:** Pure `SpellMatch` reducer (rules for all three modes, unit-tested) + `SkillGameViewModel` that loads deterministic questions from the shared seed and feeds local/peer answers into the reducer. Lobby (`LobbyFragment`/`LobbyViewModel`) hosts or joins a `Room` via `WebsocketRoomFactory`, registers/discovers the room with the existing NSD use cases from `xo/domain`, and keeps the room in a process-wide `ActiveRoom` holder so it survives the lobby → game navigation. `LeaderboardSync` is attached to every room; results are submitted at game end. Dagger module lives in the empty `feature/games/mix/domain/wiring` module (CLAUDE.md: Dagger only in wiring).

**Spec:** `docs/superpowers/specs/2026-10-31-games-tab-design.md`; mockups above.

## Rules decided here (my calls)

- Solo: 3 lives, +1 per correct answer, game over at 0 lives; score = correct answers.
- Versus: 10 rounds, same question on both phones (question `r` generated from `Random(seed + r)`). Correct and faster than the opponent's correct answer → +2, correct but slower (or opponent wrong) → +1, wrong → 0. Next round starts when both answered. Winner = higher score.
- Coop: 3 shared lives, a wrong answer by either player costs one; shared score = all correct answers; next round when both answered; game over at 0 lives.
- Leaderboard: Solo/Coop submit the (shared) score, Versus submits own score; `gameId = "spell"`.
- Peer leaves mid-game → game ends with "соперник вышел", result still submitted.
- Wi-Fi Direct discovery is not wired in the lobby: XO had it commented out and it was never verified; NSD (same Wi-Fi) + manual `ip:port` are wired. Wi-Fi Direct row from the mockup is dropped for now.

## Tasks

### Task 1: SpellMatch reducer
`feature/games/mix/ui/.../games/skillmp/SpellMatch.kt` — `data class SpellMatch(mode, round, lives, myScore, peerScore, myAnswer: Answer?, peerAnswer: Answer?, isOver, peerLeft)`, `data class Answer(correct: Boolean, timeMs: Long)`, `fun start(mode)`, `fun SpellMatch.answer(mine: Boolean, answer: Answer): SpellMatch`, `fun SpellMatch.peerLeft(): SpellMatch`, `val SpellMatch.roundComplete: Boolean`, `fun SpellMatch.nextRound(): SpellMatch`.
- [ ] Tests `SpellMatchTest`: solo lives/score/game over; versus +2/+1/0 scoring incl. both wrong; versus ends after round 10; coop shared lives and score, ends at 0 lives; round completes only after both answered (versus/coop) and immediately in solo; peer left ends the game. Fail → implement → green → commit.

### Task 2: Deterministic questions
`GetSkillsUseCase(random: Random)`: heroes sorted by name, hero/mana/wrong options drawn from `random`. Test `GetSkillsUseCaseTest` not feasible without DB fakes → verified by Task 6 device run; keep the change minimal.

### Task 3: Wiring
- `feature/games/mix/domain/wiring`: `GamesModule` providing `RoomFactory` (WebsocketRoomFactory on an app-lifetime scope), `LeaderboardRepository` (process singleton), `LeaderboardSync`, `LocalPlayerProvider`.
- `GamesDependencies` + `ApplicationComponent`: add `appInformation` so `XoModule` (discovery/registration/network state) can be included in `GamesComponent`.
- `ActiveRoom` holder (mix ui): current `Room?`, `mode`, set by lobby, read by game.

### Task 4: Lobby + mode sheet
Mode sheet on card click (Spell only; APM/XO open as before). Solo → game directly. Versus/Coop → `LobbyFragment(mode)`: tabs Создать/Найти, host shows itself + empty slot, address `ip:port`, NSD registration while Waiting; Найти lists NSD servers + manual `ip:port`; host "Начать" enabled in Ready → `room.startGame("spell", mode)`; both navigate to the game on `Playing`.

### Task 5: Game UI per mode + leaderboard
Versus header (both scores, "ответил ✓", round x/10); Coop header (shared lives, shared score); end screen with result, records submit. `LeaderboardFragment` with game/mode tabs, own row highlighted, online dot for the current peer. Card badge = own best (Solo) from the repository.

### Task 6: Verify on devices
Emulator ↔ phone (see memory xo-network-testing): Solo round, Versus full match, Coop until game over, leaderboard shows both players on both phones. Commit, push.
