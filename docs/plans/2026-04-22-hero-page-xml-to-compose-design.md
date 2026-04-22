# Design: Migrate feature/hero-page/ui from XML to Jetpack Compose

**Date:** 2026-04-22
**Scope:** `feature/hero-page/ui` presentation layer only
**Approach:** Full Compose replacement — delete all XML / ViewBinding / adapters / custom Kotlin views

---

## Goal

Replace all XML layouts, RecyclerView adapters, ViewHolders, ItemDecorations, and custom Kotlin views (`AnimatedToolbar`, `HeroFlow`, `HeroStatistics`) in `feature/hero-page/ui` with Jetpack Compose. Domain layers (`domain/api`, `domain/impl`, `domain/wiring`) are not touched. ViewModels, UseCases, VO classes, and DI component are preserved. The primary build gate is `:feature:hero-page:demo:assembleDebug` (currently failing with 53 Java compile errors in generated ViewBinding classes).

---

## Background — Current State

The module currently fails to build. Errors originate in generated Java ViewBinding classes that reference Kotlin custom views (`HeroFlow`, `HeroStatistics`, `AnimatedToolbar`, `ErrorView`, `SelectableImageView`) that can't be resolved during Java compilation. A workaround block `android { buildFeatures { dataBinding = false } }` existed in `build.gradle.kts` but was removed in uncommitted changes.

Additionally, two of three `RecyclerView.Adapter` classes (`HeroInfoAdapter`, `HeroGuideAdapter`) are currently stubbed with `TODO()` in `onCreateViewHolder` and commented-out data binding code — meaning Hero Info and Hero Guide tabs don't actually render today even when the build succeeds. Only `ResponsesAdapter` is fully functional.

Implication: this migration builds UI "from scratch" against VO classes, rather than "preserving rendering 1-for-1". We render whatever the ViewModels currently emit.

---

## Files to Delete

### XML layouts (30 files — entire `res/layout/`)

```
dialog_response.xml               item_guide_info_line.xml
flow_hero.xml                     item_guide_skill_build.xml
flow_hero_item.xml                item_guide_starting_hero_items.xml
flow_spell.xml                    item_guide_title.xml
flow_starting_hero_item.xml       item_hard_to_win_heroes.xml
fragment_hero_guide.xml           item_hero_attributes.xml
fragment_hero_info.xml            item_hero_guide_tab_number.xml
fragment_hero_page.xml            item_hero_header_playable.xml
fragment_hero_responses.xml       item_hero_spells.xml
hero_statistics.xml               item_response_body.xml
item_body_line.xml                item_response_image.xml
item_body_simple.xml              item_spell.xml
item_body_talent.xml              item_talent.xml
item_body_with_image.xml          item_easy_to_win_heroes.xml
item_guide_hero_items.xml
```

### Kotlin classes

| File | Reason |
|------|--------|
| `internal/info/adapter/HeroInfoAdapter.kt` | replaced by `HeroInfoScreen` |
| `internal/info/adapter/HeroInfoMainViewHolder.kt` | → `AttributesItem` composable |
| `internal/info/adapter/HeroInfoItemDecorator.kt` | → Compose `contentPadding` + insets |
| `internal/info/adapter/HeroSpellsViewHolder.kt` | → `SpellsRow` composable |
| `internal/info/adapter/HeroSpellsItemDecorator.kt` | → Compose spacing |
| `internal/info/adapter/SpellsListAdapter.kt` | → `LazyRow` inside `SpellsRow` |
| `internal/info/adapter/SpellViewHolder.kt` | → `SpellChip` composable |
| `internal/info/adapter/TalentViewHolder.kt` | → `TalentItem` composable |
| `internal/info/domain/adapter/HeroHeaderPlayableViewHolder.kt` | → `HeaderSoundItem` composable |
| `internal/info/HeroStatistics.kt` | → replaced by `AttributesItem`; `Statistics` data class moves into VO |
| `internal/guides/HeroGuideAdapter.kt` | replaced by `HeroGuideScreen` |
| `internal/guides/HeroGuideTabAdapter.kt` | replaced by `TabRow` in `HeroPageScreen` |
| `internal/guides/HeroFlow.kt` + `VOHeroFlow*` helpers stay | custom view deleted, VO chip types **kept** (referenced by domain layer) — move them to `domain/vo/` |
| `internal/guides/domain/adapter/HeroGuideAdapter.kt` | duplicate/misplaced — DELETE |
| `internal/responses/adapter/ResponsesAdapter.kt` | replaced by `ResponsesScreen` |
| `internal/responses/adapter/ResponseBodyViewHolder.kt` | → `ResponseBodyItem` composable |
| `internal/responses/adapter/ImageViewHolder.kt` | → `ResponseImage` composable |
| `internal/responses/adapter/ResponsesImagesAdapter.kt` | → `LazyRow` in `ResponseBodyItem` |
| `internal/responses/adapter/ResponseItemDecorator.kt` | → Compose spacing |
| `internal/responses/DialogResponse.kt` | → `ResponseDownloadDialog` composable inside `ResponsesScreen` |
| `internal/pager/AnimatedToolbar.kt` | → `HeroPageToolbar` composable |
| `internal/pager/ViewPager2Adapter.kt` (if present) | → `HorizontalPager` |

### Uncommitted change reverted

Remove the already-uncommitted deletion of `android { buildFeatures { dataBinding = false } }` from `build.gradle.kts` — after Compose migration that override becomes unnecessary (no XML → no ViewBinding codegen).

---

## Files to Keep Unchanged

- **ViewModels:** `HeroInfoViewModel`, `HeroGuideViewModel`, `ResponsesViewModel` — all `StateFlow`-based, Compose-ready.
- **VO data classes:** `VOHeroInfo`, `VOSpell`, `VOBodyLine`, `VOBodySimple`, `VOBodyWithImage`, `VOBodyTalent`, `VOGuideItem` hierarchy, `VOResponse`, `VOResponseImage`, `VOGuideTab`, `VOHeroFlow*`. **Minor edit** in `VOHeroInfo.kt`: replace `HeroStatistics.Statistics` reference with a top-level `VOHeroStatistics` data class (see below).
- **Domain layer:** all UseCases, Interactors, Workers under `guides/domain/`, `info/domain/`, `responses/domain/`.
- **DI:** `HeroPageComponent.kt`.
- **Public API:** `HeroPageProvider.kt`, `HeroPageDependencies.kt`, and the static `HeroPageFragment.getInstance(heroName: String)` entry point — signatures unchanged.
- **Infrastructure:** `AudioExoPlayer` usage, `DownloadResponseManager`, `PopUpClickableSpan`, `HeroGuideWorker`, `HeroGuideFlow`.
- **Resources:** `drawable/`, `values/strings.xml`, `values-ru/strings.xml` — unchanged (some unused entries may be cleaned up in Task 7 as best-effort, but not mandatory).

---

## VO Adjustment

`VOHeroInfo.Attributes` currently references `HeroStatistics.Statistics` (nested class inside the custom view we're deleting). Move this data class out:

```kotlin
// In info/domain/vo/VOHeroInfo.kt
data class VOHeroStatistics(val strength: Double, val agility: Double, val intelligence: Double)

sealed class VOHeroInfo {
    // …
    data class Attributes(
        val urlHeroImage: String,
        val heroStatistics: VOHeroStatistics,   // was HeroStatistics.Statistics
        val isSelected: Boolean,
    ) : VOHeroInfo()
    // …
}
```

Update `GetVOHeroDescriptionUseCase` to construct `VOHeroStatistics` instead of `HeroStatistics.Statistics`. This is the only propagating change from removing `HeroStatistics`.

---

## Files to Create

```
feature/hero-page/ui/src/main/kotlin/n7/ad2/hero/page/internal/
  pager/
    HeroPageScreen.kt            — Scaffold + TabRow + HorizontalPager(3)
    HeroPageToolbar.kt           — replaces AnimatedToolbar; hero icon rotation + locale switcher
  info/
    HeroInfoScreen.kt            — LazyColumn with sticky headers
    components/
      AttributesItem.kt          — hero stats row (replaces HeroStatistics + HeroInfoMainViewHolder)
      HeaderSoundItem.kt         — row with play icon + hotkeys
      SpellsRow.kt               — LazyRow of spells
      SpellCard.kt               — single spell (Simple) icon card
      TalentItem.kt              — 4-tier talent tree row
      BodyLineItem.kt, BodySimpleItem.kt, BodyWithImageItem.kt, BodyTalentItem.kt
  guides/
    HeroGuideScreen.kt           — LazyColumn with sticky headers
    components/
      GuideTitleItem.kt, GuideInfoLineItem.kt
      HeroChipsRow.kt            — FlowRow of easy/hard-to-win hero chips
      SpellBuildRow.kt           — FlowRow of skill order chips
      HeroItemsRow.kt, StartingItemsRow.kt — FlowRow item chips
      chips/HeroChip.kt, SpellChip.kt, ItemChip.kt, StartingItemChip.kt
  responses/
    ResponsesScreen.kt           — LazyColumn with sticky headers + dialog state
    components/
      ResponseBodyItem.kt        — text + icons row + download-progress overlay
      ResponseImage.kt           — small icon chip
      ResponseDownloadDialog.kt  — AlertDialog replacing DialogResponse
```

No new `res/layout/` files are created.

---

## Architecture

### Fragment pattern (all 4 fragments)

```kotlin
class HeroPageFragment : Fragment() {
    companion object {
        fun getInstance(heroName: String): HeroPageFragment = HeroPageFragment().apply {
            arguments = bundleOf(HERO_NAME to heroName)
        }
        private const val HERO_NAME = "HERO_NAME"
    }

    @Inject lateinit var appInformation: AppInformation
    private val heroName by lazyUnsafe { requireArguments().getString(HERO_NAME)!! }

    override fun onCreate(s: Bundle?) {
        super.onCreate(s)
        DaggerHeroPageComponent.factory().create(findDependencies()).inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, s: Bundle?): View = content {
        HeroPageScreen(
            heroName = heroName,
            appLocale = appInformation.appLocale,
            onLocaleChange = { /* propagate to child fragments via a shared VM or a callback */ },
        )
    }
}
```

`content {}` (from `core/ui`) already wraps the body in `AppTheme`. Child Fragments (`HeroInfoFragment`, `HeroGuideFragment`, `ResponsesFragment`) follow the same pattern.

### HeroPageScreen — pager container

```kotlin
@Composable
internal fun HeroPageScreen(heroName: String, appLocale: AppLocale, onLocaleChange: (AppLocale) -> Unit) {
    val pagerState = rememberPagerState(pageCount = { 3 })
    val scope = rememberCoroutineScope()
    val tabTitles = listOf(R.string.hero_info, R.string.hero_sound, R.string.hero_guide)

    Column(Modifier.fillMaxSize().statusBarsPadding()) {
        HeroPageToolbar(heroName, appLocale, pagerState.currentPage, onLocaleChange)
        TabRow(selectedTabIndex = pagerState.currentPage) {
            tabTitles.forEachIndexed { idx, titleRes ->
                Tab(
                    selected = pagerState.currentPage == idx,
                    onClick = { scope.launch { pagerState.animateScrollToPage(idx) } },
                    text = { Text(stringResource(titleRes)) },
                )
            }
        }
        HorizontalPager(state = pagerState, modifier = Modifier.weight(1f)) { page ->
            when (page) {
                0 -> AndroidFragment<HeroInfoFragment>(arguments = bundleOf(HERO_NAME to heroName))
                1 -> AndroidFragment<ResponsesFragment>(arguments = bundleOf(HERO_NAME to heroName))
                2 -> AndroidFragment<HeroGuideFragment>(arguments = bundleOf(HERO_NAME to heroName))
            }
        }
    }
}
```

**VM lifetime decision:** child Fragments (`HeroInfoFragment`, `HeroGuideFragment`, `ResponsesFragment`) are **kept** and hosted via `AndroidFragment` from `fragment-compose`. Rationale: this preserves each child Fragment's existing `DaggerHeroPageComponent.factory().create(findDependencies()).inject(this)` in `onAttach` — zero DI rewiring. Each child Fragment in turn calls `content { HeroInfoScreen(...) }` internally. The tree is:

```
HeroPageFragment.content { HeroPageScreen(...) }             // pager shell
  └── HeroPageScreen → HorizontalPager → AndroidFragment<HeroInfoFragment>
        └── HeroInfoFragment.content { HeroInfoScreen(viewModel) }
```

### HeroPageToolbar — replaces AnimatedToolbar

- Hero mini-avatar rotates with device orientation via `DisposableEffect` + `OrientationEventListener` → `Modifier.rotate(rotation)`.
- Locale switcher (RU ↔ EN) on page 1 only: `AnimatedVisibility(currentPage == 1)` + `AnimatedContent(locale)` for fade between labels. **`TickerView` is dropped** (one library dependency removed).
- Slide transitions between page-dependent elements: `AnimatedVisibility` with default slide/fade.

```kotlin
@Composable
internal fun HeroPageToolbar(heroName: String, locale: AppLocale, currentPage: Int, onLocaleChange: (AppLocale) -> Unit) {
    var rotation by remember { mutableFloatStateOf(0f) }
    val context = LocalContext.current
    DisposableEffect(Unit) {
        val listener = object : OrientationEventListener(context) {
            override fun onOrientationChanged(orientation: Int) {
                if (orientation != OrientationEventListener.ORIENTATION_UNKNOWN) rotation = 360f - orientation
            }
        }
        listener.enable()
        onDispose { listener.disable() }
    }
    TopAppBar(
        title = { Text(heroName, style = AppTheme.style.H6) },
        backgroundColor = AppTheme.color.surface,
        navigationIcon = {
            AnimatedVisibility(currentPage == 0) {
                AsyncImage(model = fullUrlHeroMinimap(heroName), contentDescription = null,
                    modifier = Modifier.rotate(rotation).size(30.dp).padding(start = 8.dp))
            }
        },
        actions = {
            AnimatedVisibility(currentPage == 1) {
                TextButton(onClick = {
                    val next = if (locale is AppLocale.Russian) AppLocale.English else AppLocale.Russian
                    onLocaleChange(next)
                }) {
                    AnimatedContent(targetState = locale.value, label = "locale") { Text(it) }
                }
            }
        },
    )
}
```

### HeroInfoScreen — LazyColumn with sticky headers

```kotlin
@Composable
internal fun HeroInfoScreen(viewModel: HeroInfoViewModel) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    when (val s = state) {
        is HeroInfoViewModel.State.Data -> LazyColumn(
            contentPadding = PaddingValues(bottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()),
        ) {
            s.list.forEach { vo ->
                when (vo) {
                    is VOHeroInfo.Header -> stickyHeader(key = vo) { HeaderItem(vo.item) }
                    else -> item(key = vo.javaClass to vo.hashCode()) { HeroInfoRow(vo, viewModel) }
                }
            }
        }
        is HeroInfoViewModel.State.Error -> ErrorScreen(s.error)
        HeroInfoViewModel.State.Loading -> LoadingAnimation()
    }
}
```

`LazyColumn.stickyHeader(key)` pins the last-seen header to the top until the next header enters. The `forEach` loop picks `stickyHeader` for `VOHeroInfo.Header` and `item` for everything else — identical behaviour to the old `StickyHeaderDecorator`.

### HeroGuideScreen — FlowRow chips

```kotlin
@Composable
internal fun HeroGuideScreen(viewModel: HeroGuideViewModel, heroName: String) {
    val items by viewModel.loadHeroWithGuides(heroName).observeAsState(emptyList())
    LazyColumn {
        items.forEach { item ->
            when (item) {
                is VOGuideTitle -> stickyHeader(key = item.title) { GuideTitleItem(item.title) }
                else -> item(key = item) { GuideRow(item) }
            }
        }
    }
}

@Composable
private fun GuideRow(item: VOGuideItem) {
    when (item) {
        is VOGuideTitle -> Unit   // handled above
        is VOGuideInfoLine -> GuideInfoLineItem(item.title)
        is VOGuideEasyToWinHeroes -> FlowRow(Modifier.padding(4.dp)) { item.list.forEach { HeroChip(it, good = true) } }
        is VOGuideHardToWinHeroes -> FlowRow(Modifier.padding(4.dp)) { item.list.forEach { HeroChip(it, good = false) } }
        is VOGuideSpellBuild      -> FlowRow(Modifier.padding(4.dp)) { item.list.forEach { SpellChip(it) } }
        is VOGuideStartingHeroItems -> FlowRow(Modifier.padding(4.dp)) { item.list.forEach { StartingItemChip(it) } }
        is VOGuideHeroItems       -> FlowRow(Modifier.padding(4.dp)) { item.list.forEach { HeroItemChip(it) } }
    }
}
```

`FlowRow` from `androidx.compose.foundation.layout.FlowRow` (stable since 1.5). Replaces `ConstraintLayout.Flow` + dynamic `addView` pattern.

### ResponsesScreen — with download dialog in state

```kotlin
@Composable
internal fun ResponsesScreen(viewModel: ResponsesViewModel, audioExoPlayer: AudioExoPlayer,
                             onDownload: (VOResponse.Body) -> Unit) {
    var dialogTarget by remember { mutableStateOf<VOResponse.Body?>(null) }
    val state by viewModel.state.collectAsStateWithLifecycle()
    when (val s = state) {
        is ResponsesViewModel.State.Data -> LazyColumn {
            s.list.forEach { r ->
                when (r) {
                    is VOResponse.Title -> stickyHeader(key = r) { HeaderItem(r.data) }
                    is VOResponse.Body  -> item(key = r.title) {
                        ResponseBodyItem(
                            item = r,
                            onClick = { audioExoPlayer.play(r.audioUrl) },
                            onLongClick = { if (!r.isSavedInMemory) dialogTarget = r },
                        )
                    }
                }
            }
        }
        is ResponsesViewModel.State.Error -> ErrorScreen(s.error)
        ResponsesViewModel.State.Loading  -> Unit
    }
    dialogTarget?.let { body ->
        ResponseDownloadDialog(
            onConfirm = { onDownload(body); dialogTarget = null },
            onDismiss = { dialogTarget = null },
        )
    }
}
```

`DialogFragment` → Compose state. `AudioExoPlayer` stays as-is, constructed in Fragment, passed into the Composable.

---

## Behaviour Preserved

| Feature | Old | New |
|---------|-----|-----|
| 3-tab pager | `ViewPager2` + `TabLayoutMediator` | `HorizontalPager` + `TabRow` + `rememberPagerState` |
| Tab labels | `TabLayoutMediator { tab.text = … }` | `Tab(text = { Text(stringResource(…)) })` |
| Page-dependent toolbar | `AnimatedToolbar.pageSelected(pos)` + `TransitionManager.Slide` | `AnimatedVisibility(currentPage == N)` |
| Hero icon gyro rotation | `OrientationEventListener` → `ivHero.rotation` | same listener in `DisposableEffect` → `Modifier.rotate` |
| Locale switcher animation | `TickerView` (external lib) | `AnimatedContent` fade |
| Hero Info list | `RecyclerView` + `HeroInfoAdapter` (stubbed) | `LazyColumn` |
| Hero Guide list | `RecyclerView` + `HeroGuideAdapter` (stubbed) + `HeroFlow` | `LazyColumn` + `FlowRow` |
| Responses list | `RecyclerView` + `ResponsesAdapter` (working) + `ListAdapter` diffing | `LazyColumn` with `items(key = …)` — Compose diffs automatically |
| Sticky headers | `StickyHeaderDecorator` (custom) | `LazyColumn.stickyHeader(key)` (standard API) |
| Download dialog | `DialogFragment` + `setFragmentResult` | Compose `AlertDialog` in state |
| Audio playback | `AudioExoPlayer` called from Fragment | `AudioExoPlayer` called from Fragment, passed to Composable |
| Error state | `n7.ad2.ui.ErrorView` (custom View) | `ErrorScreen` Composable (exists in `core/ui`) |
| Loading state | (nothing / commented) | `LoadingAnimation` Composable (exists in `core/ui`) |
| Insets | `ViewCompat.setOnApplyWindowInsetsListener` | `Modifier.statusBarsPadding()` / `WindowInsets.navigationBars` |
| Popup info (long-press on span) | `InfoPopupWindow` + `PopUpClickableSpan` | `AndroidView` wrapper around `TextView` for `SpannableString` bodies — Compose `Text` can't render `ImageSpan` natively. Other text renders via `Text()`. |

---

## Known Risks & Mitigations

1. **Spannable bodies (`VOBodyWithImage.body: SpannableString`, `VOBodyLine.title: SpannableString`)** — Compose `Text` doesn't render `ImageSpan` or custom spans (`PopUpClickableSpan`). Mitigation: wrap those specific items in `AndroidView { TextView(context).apply { text = spannable; movementMethod = LinkMovementMethod.getInstance() } }`. Only used in 2 of ~9 item types, so Compose-first everywhere else. This is documented as an explicit per-item decision in the implementation plan.
2. **Child VM scoping — resolved.** See "HeroPageScreen" above: child Fragments are kept and hosted via `AndroidFragment`. Each child Fragment's DI wiring (`onAttach` + Dagger inject) is untouched.
3. **`content {}` vs `Fragment(R.layout.xxx)`** — existing Fragments use the `Fragment(layoutRes)` ctor. Switching to `onCreateView` override that returns `ComposeView` from `content {}`. No DI change.
4. **`convention.compose` already adds the Compose bundle** (includes `composeCoil`) — no separate `libs.composeCoil` line needed.
5. **AGP Java-Kotlin compile ordering bug** causing the current build breakage becomes moot — after migration there are no generated ViewBinding Java classes that reference Kotlin symbols.
6. **`fragment-compose` dependency** — `libs.fragment-compose` exists in version catalog. Needs to be added to `implementation` in `build.gradle.kts`.

---

## Test Strategy

- **No existing tests on UI** — this module has no `*ViewHolder` tests, no screenshot tests, no instrumented UI tests. `testImplementation(libs.test.*)` deps are declared but no test files exist in `feature/hero-page/ui/src/test/` or `/src/androidTest/`. Verified by `ls feature/hero-page/ui/src/`.
- **No new Compose tests** in this migration. Out of scope.
- **Verification via build** — `:feature:hero-page:ui:compileDebugKotlin` after each Task; `:feature:hero-page:demo:assembleDebug` is the final gate.
- **`@Preview`** annotations added for `HeroPageToolbar`, `HeroInfoScreen(state = fake Data)`, `HeroGuideScreen`, `ResponsesScreen`, and a few key leaf composables to enable visual check in Android Studio.
- **Smoke run** — `HeroPageActivityDemo` is empty (does not actually open `HeroPageFragment`), so runtime rendering is *not* verified by the demo app. The demo only validates that everything compiles and links. This is called out as a known limitation.

---

## Execution Order (task-by-task, each a commit)

1. **Bootstrap build config.** Add `convention.compose` plugin + `libs.fragment-compose` dep. Do not yet touch Kotlin code. Expected: `:feature:hero-page:ui:assembleDebug` *still fails* — the previously-failing ViewBinding gen is still there until XMLs are deleted. OK — checkpoint commit.
2. **VO refactor.** Move `HeroStatistics.Statistics` → `VOHeroStatistics`. Update `GetVOHeroDescriptionUseCase`. Compile still fails (expected).
3. **Hero Info migration.** Create `HeroInfoScreen` + components. Rewrite `HeroInfoFragment.onCreateView → content {}`. Delete `info/adapter/`, `info/domain/adapter/`, `HeroStatistics.kt`, `fragment_hero_info.xml`, all `item_hero_*.xml`, `item_body_*.xml`, `hero_statistics.xml`. Checkpoint: `:feature:hero-page:ui:compileDebugKotlin` should pass.
4. **Hero Guide migration.** Create `HeroGuideScreen` + chip components. Rewrite `HeroGuideFragment`. Delete `HeroGuideAdapter`, `HeroGuideTabAdapter`, `HeroFlow.kt`, `guides/domain/adapter/`, `fragment_hero_guide.xml`, `flow_*.xml`, `item_guide_*.xml`, `item_easy_to_win_heroes.xml`, `item_hard_to_win_heroes.xml`, `item_hero_guide_tab_number.xml`.
5. **Responses migration.** Create `ResponsesScreen` + `ResponseDownloadDialog`. Rewrite `ResponsesFragment`. Delete `responses/adapter/`, `DialogResponse.kt`, `fragment_hero_responses.xml`, `dialog_response.xml`, `item_response_*.xml`.
6. **Hero Page pager migration.** Create `HeroPageScreen` + `HeroPageToolbar`. Rewrite `HeroPageFragment`. Delete `AnimatedToolbar.kt`, `fragment_hero_page.xml`.
7. **Final cleanup.** Verify `res/layout/` is empty (remove empty dir). Remove the uncommitted `dataBinding = false` block (no longer needed). Run `./gradlew :feature:hero-page:demo:assembleDebug` — **must pass**.

Each task is designed to be independently commit-able with a green `compileDebugKotlin` checkpoint (Task 1 being the only exception — it intentionally leaves the build broken as a bootstrap step).

---

## Out of Scope

- Domain layer (`domain/api`, `domain/impl`, `domain/wiring`) — not modified.
- Public API (`HeroPageProvider`, `HeroPageFragment.getInstance`) — signature preserved.
- `feature/heroes/ui` migration — tracked by a separate design doc (`2026-03-09-heroes-xml-to-compose-design.md`).
- Unit / UI / screenshot tests for new Composables.
- Restoring functionality that is currently broken due to `TODO()` in `HeroInfoAdapter` and `HeroGuideAdapter`. We build new UI straight from VOs; if ViewModels emit incomplete data, that's a separate follow-up.
- Changes to `core/ui`.
- Migration of other features (`feature/items`, `feature/streams`, etc.) still using XML.
- Accessibility / RTL / tablet layout adjustments beyond what `AppTheme` defaults provide.
- Theme refactor (Material2 → Material3). Project stays on Material2.