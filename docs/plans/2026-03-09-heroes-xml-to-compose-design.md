# Design: Migrate feature/heroes/ui from XML to Jetpack Compose

**Date:** 2026-03-09
**Scope:** `feature/heroes/ui` presentation layer only
**Approach:** Full Compose replacement (Variant A)

---

## Goal

Replace all XML layouts, RecyclerView adapter, ViewHolder, and ItemDecoration in `feature/heroes/ui` with Jetpack Compose, preserving all existing functionality. The domain layer (`domain/api`, `domain/impl`, `domain/wiring`) is not touched.

---

## Files to Delete

| File | Replacement |
|------|-------------|
| `res/layout/fragment_heroes.xml` | `content {}` in Fragment |
| `res/layout/item_hero_body.xml` | `@Composable HeroItem` |
| `internal/adapter/HeroesListAdapter.kt` | `LazyVerticalGrid` |
| `internal/adapter/HeroBodyViewHolder.kt` | `@Composable HeroItem` |
| `internal/adapter/HeroesItemDecorator.kt` | Compose padding + insets |

## Files to Keep Unchanged

- `HeroesViewModel.kt` — StateFlow-based, Compose-ready as-is
- `VOHero.kt` — sealed class maps naturally to LazyVerticalGrid spans
- `FilterHeroesUseCase.kt`, `GetVOHeroesListUseCase.kt`
- `HeroesComponent.kt`, `HeroesDependencies.kt`
- `HeroesProvider.kt`, `HeroesFragmentFactory.kt`
- All tests (`HeroesViewModelTest`, `FragmentTest`)

## Files to Create

```
ui/internal/compose/
├── HeroesScreen.kt   — root composable (grid, insets, drawer percent)
└── HeroItem.kt       — single hero card composable
```

## Files to Modify

- `HeroesFragment.kt` — switch to `content {}`, remove binding/adapter setup
- `build.gradle.kts` — add `convention.compose` plugin + `libs.composeCoil`

---

## Architecture

### HeroesFragment (after)

```kotlin
override fun onCreateView(...): View = content {
    val heroes by viewModel.heroes.collectAsState()
    HeroesScreen(
        heroes = heroes,
        drawerPercentListener = parentFragment as DrawerPercentListener,
        onHeroClicked = ::onHeroClicked,
    )
}

private fun onHeroClicked(hero: VOHero.Body) {
    getMainFragmentNavigator?.setMainFragment(
        navigator.get().heroPageApi.getPagerFragment(hero.name)
    ) { addToBackStack(null) }
    if (!hero.viewedByUser) viewModel.updateViewedByUserFieldForHero(hero.name)
}
```

### HeroesScreen

- `LazyVerticalGrid(columns = Fixed(3))`
- `VOHero.Header` → `item(span = { GridItemSpan(maxLineSpan) })` — full-width header
- `VOHero.Body` → `item { HeroItem(...) }`
- `DrawerPercentListener` via `DisposableEffect` (same pattern as `NewsScreen`)
- Top/bottom padding driven by `WindowInsets.statusBars` / `WindowInsets.navigationBars` scaled by `drawerPercent`

### HeroItem

```kotlin
Box(modifier = Modifier.fillMaxWidth().aspectRatio(0.75f)) {
    AsyncImage(
        model = hero.imageUrl,
        placeholder = painterResource(R.drawable.stream_placeholder),
        contentDescription = hero.name,
        contentScale = ContentScale.Crop,
        modifier = Modifier.fillMaxSize(),
    )
    // Viewed indicator — replaces SelectableImageView border animation
    if (hero.viewedByUser) {
        Box(
            modifier = Modifier
                .width(3.dp)
                .fillMaxHeight()
                .background(MaterialTheme.colorScheme.primary)
                .align(Alignment.CenterStart)
        )
    }
    Text(
        text = hero.name,
        style = MaterialTheme.typography.labelSmall,
        color = Color.White,
        modifier = Modifier
            .align(Alignment.BottomCenter)
            .padding(bottom = 4.dp),
    )
}
```

### DrawerPercent (same pattern as NewsScreen)

```kotlin
DisposableEffect(Unit) {
    drawerPercentListener.setDrawerPercentListener { percent -> drawerPercent = percent }
    onDispose { drawerPercentListener.setDrawerPercentListener(null) }
}
```

### build.gradle.kts changes

```kotlin
plugins {
    id("convention.android-library")
    id("convention.compose")        // ADD
    id("n7.plugins.kotlin-ksp")
}

dependencies {
    // ... existing deps ...
    implementation(libs.composeCoil) // ADD — for AsyncImage
}
```

---

## Behaviour Preserved

| Feature | Old implementation | New implementation |
|---------|-------------------|-------------------|
| 3-column grid | `GridLayoutManager(3)` | `LazyVerticalGrid(Fixed(3))` |
| Attribute group headers | `VOHero.Header` + span=3 in adapter | `item(span = GridItemSpan(maxLineSpan))` |
| Hero image loading | `ImageView.load()` via Coil | `AsyncImage` via coil-compose |
| Viewed indicator | `SelectableImageView` animated border | `Box` with 3dp primary color left edge |
| Hero click → navigation | `View.OnClickListener` on card | `Modifier.clickable` on `HeroItem` |
| Mark as viewed on click | `viewModel.updateViewedByUserFieldForHero` | Same ViewModel call |
| Window insets (status/nav bar) | `ViewCompat.setOnApplyWindowInsetsListener` | `WindowInsets.statusBars` / `navigationBars` |
| Drawer percent spacing | `DrawerPercentListener` → `HeroesItemDecorator.percent` | `DisposableEffect` → Compose state |

---

## Out of Scope

- Domain layer (no changes)
- `HeroesViewModel` (no changes)
- `VOHero` sealed class (no changes)
- Demo app (no changes needed)
- Android instrumented tests — keep existing `FragmentTest` as-is
