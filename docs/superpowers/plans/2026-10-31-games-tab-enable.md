# Games Tab: Enable and Verify (Plan 1 of 4) Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Turn the Games tab on, make the three existing games (Угадай спелл, Kill Creep, крестики-нолики) reachable from it and confirmed working on a device.

**Architecture:** The tab is gated by the remote `settings.json` (fetched from GitHub master by `SettingsApi`) with a local fallback in `GetMenuItemsUseCase`. `GamesFragment` (in `feature/games/mix/ui`) maps `GameVO` to fragments. XO is currently wired only in its own demo app, so it must be connected to the main app through `XoApi`/`XoProvider`.

**Tech Stack:** Kotlin, Dagger 2 (KSP), Compose, Fragment navigation via `core/navigator`.

**Spec:** `docs/superpowers/specs/2026-10-31-games-tab-design.md`

## Global Constraints

- Build only through the IDE (`studio_gradle_build`), not `gradlew` in the shell (project memory).
- Dagger may be used only in `domain/wiring` and existing component files; do not add it elsewhere (CLAUDE.md).
- Wi-Fi Direct cannot be tested on the emulator; websocket path can.
- Do not touch the working transports in `feature/games/xo/domain/impl`.

## Review Focus

- Offline start with no cached settings: tab state comes from `defaultMenu` and must match `settings.json` (both enabled).
- Old cached settings (`isEnable=false` for GAMES) in `Preference`: tab stays hidden until the cache refreshes. Expected: documented, not a bug (`isNeedToUpdateSettings` handles refresh).
- XO opened without Wi-Fi / Wi-Fi Direct permissions: screen must show a state, not crash.
- Back from any game returns to the games list.

---

### Task 1: Enable the tab

**Files:**
- Modify: `settings.json` (GAMES `isEnable` -> `true`)
- Modify: `feature/drawer/src/main/kotlin/n7/ad2/drawer/internal/domain/usecase/GetMenuItemsUseCase.kt:35` (`Menu(VOMenuType.GAMES, false)` -> `Menu(VOMenuType.GAMES)`, matching the other entries)

**Interfaces:**
- Produces: GAMES menu item enabled both remotely and in the offline fallback.

- [ ] **Step 1: Edit both places as above.**
- [ ] **Step 2: Build `:feature:drawer` with `studio_gradle_build`. Expected: success.**
- [ ] **Step 3: Install the debug app on the emulator, clear app data, open the drawer. Expected: "Игры" item is enabled and opens the games list. (The remote file only changes after the commit is pushed; the offline fallback is what you see locally if the network fetch fails or you test with airplane mode.)**
- [ ] **Step 4: Commit `settings.json` and `GetMenuItemsUseCase.kt` with message `enable Games tab`.**

### Task 2: Verify Угадай спелл and Kill Creep

**Files:**
- Modify only what is broken; start from `feature/games/mix/ui/src/main/kotlin/n7/ad2/games/internal/games/skillmp/` and `.../killCreep/`.

**Interfaces:**
- Consumes: `GamesFragment.onGameClicked` routing (`GameVO.GuessSkillMana` -> `SkillGameFragment`, `GameVO.Apm` -> `KillCreepFragment`).

- [ ] **Step 1: On the emulator open each game, play one round, press back. Record every crash or blank screen with `studio_read_logcat` (filter by app package).**
- [ ] **Step 2: For each failure, fix the root cause (use superpowers:systematic-debugging), rebuild, repeat Step 1 for that game until a full round plays and back returns to the list.**
- [ ] **Step 3: Commit per game: `fix games: <game> <cause>`.**

### Task 3: Wire XO into the main app

**Files:**
- Modify: `feature/games/mix/ui/build.gradle.kts` (add `implementation(projects.feature.games.xo.ui)`)
- Modify: `feature/games/mix/ui/src/main/kotlin/n7/ad2/games/internal/GamesViewModel.kt` (add `GameVO.XO(res)` to the list)
- Modify: `feature/games/mix/ui/src/main/kotlin/n7/ad2/games/internal/GamesFragment.kt` (`is GameVO.XO ->` fragment from `XoProvider().getFragment()`; the `else -> TODO()` branch is replaced so no `TODO()` remains reachable from the list)
- Modify: the games dependency provider that already supplies `XoDependencies` fields, mirroring `feature/games/xo/demo/.../di/XoApplicationComponent.kt`; the xo `HasDependencies` map must be reachable via `findDependencies()` from the xo fragment.

**Interfaces:**
- Consumes: `n7.ad2.xo.api.XoProvider.getFragment(): Fragment`, `n7.ad2.xo.api.XoDependencies` (read its members and satisfy each from the app graph).
- Produces: a XO card in the games list that opens the XO fragment.

- [ ] **Step 1: Add the dependency and the `GameVO.XO` card; build `:feature:games:mix:ui`. Expected: compiles.**
- [ ] **Step 2: Provide `XoDependencies` from the app (copy the shape from the demo component, reuse existing core bindings where the demo used fakes).**
- [ ] **Step 3: Build `:app`, open the XO card on the emulator. Expected: the XO screen renders (server list / connect UI) with no crash, including when permissions are denied.**
- [ ] **Step 4: Verify websocket path: emulator hosts, real phone joins on the same Wi-Fi (emulator needs host-port forwarding or bridged networking; record the working setup in `docs/superpowers/specs/2026-10-31-games-tab-design.md` under "Проверка"). Expected: one move made on each side appears on both.**
- [ ] **Step 5: Commit: `wire XO into games list`.**

## Self-Review

- Spec step 1 ("Включить вкладку, проверить три игры") is covered by Tasks 1-3.
- Spec steps 2-6 (shared network layer, leaderboard, new games) are deliberately **not** in this plan: they depend on what Tasks 2-3 find broken in the transport and fragment wiring. Plan 2 (network layer + room protocol), Plan 3 (leaderboard sync), Plan 4 (games in Solo/Versus/Coop) will be written after this one lands.
