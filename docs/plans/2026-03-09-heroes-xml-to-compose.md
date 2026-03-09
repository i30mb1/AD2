# Heroes UI: XML → Compose Migration Implementation Plan

> **For Claude:** REQUIRED SUB-SKILL: Use superpowers:executing-plans to implement this plan task-by-task.

**Goal:** Replace all XML layouts, RecyclerView adapter, ViewHolder, and ItemDecoration in `feature/heroes/ui` with Jetpack Compose, preserving all existing functionality.

**Architecture:** `HeroesFragment` switches to `content {}` and hosts `HeroesScreen` composable. `LazyVerticalGrid` replaces `RecyclerView` + `GridLayoutManager`. `VOHero` sealed class maps directly to grid items via `GridItemSpan`. No changes to ViewModel or domain layers.

**Tech Stack:** Jetpack Compose, `LazyVerticalGrid`, `coil-compose` (`AsyncImage`), `WindowInsets`, `DisposableEffect` for `DrawerPercentListener`.

**Design doc:** `docs/plans/2026-03-09-heroes-xml-to-compose-design.md`

---

## Context: Key Files

```
feature/heroes/ui/
  build.gradle.kts                            ← modify: add compose plugin + coil-compose
  src/main/kotlin/n7/ad2/feature/heroes/ui/
    internal/
      HeroesFragment.kt                       ← rewrite
      adapter/
        HeroesListAdapter.kt                  ← DELETE
        HeroBodyViewHolder.kt                 ← DELETE
        HeroesItemDecorator.kt               ← DELETE
      compose/
        HeroesScreen.kt                       ← CREATE
        HeroItem.kt                           ← CREATE
  src/main/res/layout/
    fragment_heroes.xml                       ← DELETE
    item_hero_body.xml                        ← DELETE
```

**Keep unchanged:** `HeroesViewModel`, `VOHero`, `FilterHeroesUseCase`, `GetVOHeroesListUseCase`, `HeroesComponent`, `HeroesDependencies`, `HeroesProvider`, `HeroesFragmentFactory`, all tests.

---

## Task 1: Update build.gradle.kts

**Files:**
- Modify: `feature/heroes/ui/build.gradle.kts`

**Step 1: Add `convention.compose` plugin and `composeCoil` dependency**

Open `feature/heroes/ui/build.gradle.kts`. The file currently looks like:

```kotlin
plugins {
    id("convention.android-library")
    id("n7.plugins.kotlin-ksp")
}

dependencies {
    implementation(projects.core.commonAndroid)
    implementation(projects.core.dagger)
    implementation(projects.core.coroutines)
    implementation(projects.core.logger.appLogger)
    implementation(projects.core.navigator)
    implementation(projects.core.ui)

    implementation(projects.feature.heroes.domain.api)

    ksp(libs.daggerAnnotation)

    androidTestImplementation(libs.test.fragment)
    androidTestImplementation(libs.test.junit.kotlin)
    androidTestImplementation(libs.test.mockk)
    androidTestImplementation(libs.test.espresso)

    testImplementation(libs.test.junit)
    testImplementation(libs.test.coroutines)
    testImplementation(libs.test.truth.jvm)
    testImplementation(testFixtures(projects.core.logger.appLogger))
    testImplementation(testFixtures(projects.feature.heroes.domain.impl))
}
```

Replace with:

```kotlin
plugins {
    id("convention.android-library")
    id("convention.compose")
    id("n7.plugins.kotlin-ksp")
}

dependencies {
    implementation(projects.core.commonAndroid)
    implementation(projects.core.dagger)
    implementation(projects.core.coroutines)
    implementation(projects.core.logger.appLogger)
    implementation(projects.core.navigator)
    implementation(projects.core.ui)

    implementation(projects.feature.heroes.domain.api)

    implementation(libs.composeCoil)

    ksp(libs.daggerAnnotation)

    androidTestImplementation(libs.test.fragment)
    androidTestImplementation(libs.test.junit.kotlin)
    androidTestImplementation(libs.test.mockk)
    androidTestImplementation(libs.test.espresso)

    testImplementation(libs.test.junit)
    testImplementation(libs.test.coroutines)
    testImplementation(libs.test.truth.jvm)
    testImplementation(testFixtures(projects.core.logger.appLogger))
    testImplementation(testFixtures(projects.feature.heroes.domain.impl))
}
```

**Step 2: Verify module syncs**

Run:
```
.\gradlew :feature:heroes:ui:dependencies --configuration debugCompileClasspath
```
Expected: output contains `io.coil-kt:coil-compose`.

**Step 3: Commit**

```bash
git add feature/heroes/ui/build.gradle.kts
git commit -m "feat(heroes/ui): add compose plugin and coil-compose dependency"
```

---

## Task 2: Create HeroItem composable

**Files:**
- Create: `feature/heroes/ui/src/main/kotlin/n7/ad2/feature/heroes/ui/internal/compose/HeroItem.kt`

**Step 1: Create the file**

```kotlin
package n7.ad2.feature.heroes.ui.internal.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import n7.ad2.feature.heroes.ui.R
import n7.ad2.feature.heroes.ui.internal.domain.vo.VOHero
import n7.ad2.ui.compose.AppTheme

@Composable
internal fun HeroItem(
    hero: VOHero.Body,
    onClick: (VOHero.Body) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(0.75f)
            .clickable { onClick(hero) },
    ) {
        AsyncImage(
            model = hero.imageUrl,
            contentDescription = hero.name,
            contentScale = ContentScale.Crop,
            placeholder = painterResource(R.drawable.stream_placeholder),
            error = painterResource(R.drawable.stream_placeholder),
            modifier = Modifier.fillMaxSize(),
        )
        if (hero.viewedByUser) {
            Box(
                modifier = Modifier
                    .width(3.dp)
                    .fillMaxHeight()
                    .background(MaterialTheme.colorScheme.primary)
                    .align(Alignment.CenterStart),
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
}

@Preview
@Composable
private fun HeroItemPreview() {
    AppTheme {
        HeroItem(
            hero = VOHero.Body(
                name = "Anti-Mage",
                imageUrl = "",
                viewedByUser = true,
            ),
            onClick = {},
        )
    }
}
```

**Note on placeholder:** The drawable `R.drawable.stream_placeholder` is already in the module (referenced in `HeroBodyViewHolder`). If the build complains it doesn't exist in `feature/heroes/ui`, check `feature/streams/src/main/res/drawable/` and copy/move it, or use any existing placeholder from `core.ui`.

**Step 2: Verify it compiles**

```
.\gradlew :feature:heroes:ui:compileDebugKotlin
```
Expected: BUILD SUCCESSFUL

**Step 3: Commit**

```bash
git add feature/heroes/ui/src/main/kotlin/n7/ad2/feature/heroes/ui/internal/compose/HeroItem.kt
git commit -m "feat(heroes/ui): add HeroItem composable"
```

---

## Task 3: Create HeroesScreen composable

**Files:**
- Create: `feature/heroes/ui/src/main/kotlin/n7/ad2/feature/heroes/ui/internal/compose/HeroesScreen.kt`

**Step 1: Create the file**

```kotlin
package n7.ad2.feature.heroes.ui.internal.compose

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import n7.ad2.android.DrawerPercentListener
import n7.ad2.feature.heroes.ui.internal.domain.vo.VOHero
import n7.ad2.ui.compose.AppTheme

@Composable
internal fun HeroesScreen(
    heroes: List<VOHero>,
    drawerPercentListener: DrawerPercentListener,
    onHeroClicked: (VOHero.Body) -> Unit,
    modifier: Modifier = Modifier,
) {
    var drawerPercent by remember { mutableStateOf(0f) }

    val statusBarsInsets = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
    val navigationBarsInsets = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()

    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        modifier = modifier,
        contentPadding = PaddingValues(
            top = 6.dp + statusBarsInsets * drawerPercent,
            bottom = 6.dp + navigationBarsInsets * drawerPercent,
            start = 3.dp,
            end = 3.dp,
        ),
    ) {
        items(
            items = heroes,
            key = { hero ->
                when (hero) {
                    is VOHero.Body -> hero.name
                    is VOHero.Header -> hero.data.title
                }
            },
            span = { hero ->
                when (hero) {
                    is VOHero.Body -> GridItemSpan(1)
                    is VOHero.Header -> GridItemSpan(maxLineSpan)
                }
            },
        ) { hero ->
            when (hero) {
                is VOHero.Body -> HeroItem(hero = hero, onClick = onHeroClicked)
                is VOHero.Header -> Text(
                    text = hero.data.title,
                    style = AppTheme.style.header,
                    modifier = Modifier,
                )
            }
        }
    }

    DisposableEffect(Unit) {
        drawerPercentListener.setDrawerPercentListener { percent -> drawerPercent = percent }
        onDispose { drawerPercentListener.setDrawerPercentListener(null) }
    }
}
```

**Note on header style:** `AppTheme.style.header` may or may not exist in your `AppTheme`. Check `core/ui/src/main/kotlin/n7/ad2/core/ui/compose/AppTypography.kt` for available styles. Use `MaterialTheme.typography.titleMedium` as fallback if needed.

**Step 2: Verify it compiles**

```
.\gradlew :feature:heroes:ui:compileDebugKotlin
```
Expected: BUILD SUCCESSFUL

**Step 3: Commit**

```bash
git add feature/heroes/ui/src/main/kotlin/n7/ad2/feature/heroes/ui/internal/compose/HeroesScreen.kt
git commit -m "feat(heroes/ui): add HeroesScreen composable with LazyVerticalGrid"
```

---

## Task 4: Rewrite HeroesFragment

**Files:**
- Modify: `feature/heroes/ui/src/main/kotlin/n7/ad2/feature/heroes/ui/internal/HeroesFragment.kt`

**Step 1: Replace HeroesFragment content**

The current file has 118 lines with binding, adapter, and RecyclerView setup. Replace entirely with:

```kotlin
package n7.ad2.feature.heroes.ui.internal

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.fragment.app.Fragment
import dagger.Lazy
import n7.ad2.android.DependenciesMap
import n7.ad2.android.DrawerPercentListener
import n7.ad2.android.HasDependencies
import n7.ad2.android.findDependencies
import n7.ad2.android.getMainFragmentNavigator
import n7.ad2.feature.heroes.ui.internal.compose.HeroesScreen
import n7.ad2.feature.heroes.ui.internal.domain.vo.VOHero
import n7.ad2.heroes.ui.internal.di.DaggerHeroesComponent
import n7.ad2.ktx.lazyUnsafe
import n7.ad2.ktx.viewModel
import n7.ad2.navigator.Navigator
import n7.ad2.ui.content
import javax.inject.Inject
import javax.inject.Provider

/**
 * @JvmOverloads - нужен для запуска этого фрагмента когда фарбирка для HeroesFragment не будет предоставлена
 * обычно она будет предоставлена в тестах, в проде зависимости берутся из Application
 */
internal class HeroesFragment @JvmOverloads constructor(override var dependenciesMap: DependenciesMap = emptyMap()) :
    Fragment(),
    HasDependencies {

    @Inject lateinit var navigator: Lazy<Navigator>
    @Inject lateinit var heroesViewModelFactory: Provider<HeroesViewModel.Factory>

    private val viewModel: HeroesViewModel by viewModel { heroesViewModelFactory.get().create() }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        DaggerHeroesComponent.factory().create(findDependencies()).inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View = content {
        val heroes by viewModel.heroes.collectAsState()
        HeroesScreen(
            heroes = heroes,
            drawerPercentListener = parentFragment as DrawerPercentListener,
            onHeroClicked = ::onHeroClicked,
        )
    }

    private fun onHeroClicked(hero: VOHero.Body) {
        getMainFragmentNavigator?.setMainFragment(navigator.get().heroPageApi.getPagerFragment(hero.name)) {
            addToBackStack(null)
        }
        if (!hero.viewedByUser) viewModel.updateViewedByUserFieldForHero(hero.name)
    }
}
```

**Step 2: Verify it compiles**

```
.\gradlew :feature:heroes:ui:compileDebugKotlin
```
Expected: BUILD SUCCESSFUL. Warnings about unused imports from the old code are fine at this stage.

**Step 3: Commit**

```bash
git add feature/heroes/ui/src/main/kotlin/n7/ad2/feature/heroes/ui/internal/HeroesFragment.kt
git commit -m "feat(heroes/ui): rewrite HeroesFragment to use Compose"
```

---

## Task 5: Delete old XML and adapter files

**Files to delete:**
- `feature/heroes/ui/src/main/res/layout/fragment_heroes.xml`
- `feature/heroes/ui/src/main/res/layout/item_hero_body.xml`
- `feature/heroes/ui/src/main/kotlin/n7/ad2/feature/heroes/ui/internal/adapter/HeroesListAdapter.kt`
- `feature/heroes/ui/src/main/kotlin/n7/ad2/feature/heroes/ui/internal/adapter/HeroBodyViewHolder.kt`
- `feature/heroes/ui/src/main/kotlin/n7/ad2/feature/heroes/ui/internal/adapter/HeroesItemDecorator.kt`

**Step 1: Delete the files**

```bash
rm feature/heroes/ui/src/main/res/layout/fragment_heroes.xml
rm feature/heroes/ui/src/main/res/layout/item_hero_body.xml
rm feature/heroes/ui/src/main/kotlin/n7/ad2/feature/heroes/ui/internal/adapter/HeroesListAdapter.kt
rm feature/heroes/ui/src/main/kotlin/n7/ad2/feature/heroes/ui/internal/adapter/HeroBodyViewHolder.kt
rm feature/heroes/ui/src/main/kotlin/n7/ad2/feature/heroes/ui/internal/adapter/HeroesItemDecorator.kt
```

**Step 2: Full build verification**

```
.\gradlew :feature:heroes:ui:assembleDebug
```
Expected: BUILD SUCCESSFUL

If build fails with "unresolved reference: stream_placeholder" — the drawable was coming from another module. Fix: copy the placeholder drawable to `feature/heroes/ui/src/main/res/drawable/` or replace `R.drawable.stream_placeholder` in `HeroItem.kt` with `n7.ad2.core.ui.R.drawable.<available_placeholder>` (check `core/ui/src/main/res/drawable/`).

**Step 3: Run unit tests**

```
.\gradlew :feature:heroes:ui:test
```
Expected: All `HeroesViewModelTest` tests pass (they test ViewModel logic only, no UI code).

**Step 4: Commit**

```bash
git add -A
git commit -m "feat(heroes/ui): delete XML layouts and RecyclerView adapter code"
```

---

## Task 6: Fix header text style (if needed)

**Files:**
- Check: `core/ui/src/main/kotlin/n7/ad2/core/ui/compose/AppTypography.kt`
- Maybe modify: `feature/heroes/ui/src/main/kotlin/n7/ad2/feature/heroes/ui/internal/compose/HeroesScreen.kt`

**Step 1: Check what styles are available in AppTheme**

Read `core/ui/src/main/kotlin/n7/ad2/core/ui/compose/AppTypography.kt`.

If `AppTheme.style.header` does not exist, replace in `HeroesScreen.kt`:

```kotlin
// Replace:
style = AppTheme.style.header,

// With:
style = MaterialTheme.typography.titleMedium,
```

Also add import if missing:
```kotlin
import androidx.compose.material3.MaterialTheme
```

**Step 2: Final full build**

```
.\gradlew :feature:heroes:ui:assembleDebug
```
Expected: BUILD SUCCESSFUL, no errors.

**Step 3: Commit if changes were needed**

```bash
git add feature/heroes/ui/src/main/kotlin/n7/ad2/feature/heroes/ui/internal/compose/HeroesScreen.kt
git commit -m "fix(heroes/ui): use correct text style for attribute group headers"
```

---

## Summary

After all tasks complete, the `feature/heroes/ui` module will:
- Have zero XML layout files
- Have zero RecyclerView/Adapter/ViewHolder/ItemDecoration code
- Use `LazyVerticalGrid` with `DrawerPercentListener` via `DisposableEffect`
- Use `AsyncImage` (coil-compose) for hero images
- Show a 3dp primary-color left border on visited heroes
- Pass all existing `HeroesViewModelTest` unit tests
- Compile and assemble successfully
