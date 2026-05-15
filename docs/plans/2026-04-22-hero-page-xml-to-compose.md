# Hero Page UI: XML → Compose Migration Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Replace every XML layout, RecyclerView adapter, ViewHolder, ItemDecoration, and custom Kotlin View in `feature/hero-page/ui` with Jetpack Compose. Fix the failing `:feature:hero-page:demo:assembleDebug` build.

**Architecture:** Each of the 4 fragments keeps its existing `onAttach` DI wiring but switches its view creation to `content { <ScreenComposable> }` from `core/ui`. The pager fragment hosts the 3 child fragments via `AndroidFragment` from `fragment-compose`. ViewModels, VO classes, DI, and public API are untouched.

**Tech Stack:** Jetpack Compose (Material2), `androidx.compose.foundation.pager.HorizontalPager`, `LazyColumn.stickyHeader`, `FlowRow`, `coil.compose.AsyncImage`, `androidx.fragment.compose.AndroidFragment`.

**Design doc:** `docs/plans/2026-04-22-hero-page-xml-to-compose-design.md`

---

## Context: Repo Conventions

- **Compose bundle:** `id("convention.compose")` sets `buildFeatures { compose = true }`, applies the Compose compiler plugin, and adds the `libs.compose` bundle (which already includes `composeCoil`). Used in `feature/heroes/ui/build.gradle.kts` — reference that file for patterns.
- **Theme wrapper:** `core.ui`'s `Fragment.content { … }` extension wraps body in `AppTheme { … }`. Available styles: `AppTheme.style.H5`, `AppTheme.style.H6`, `AppTheme.style.body`, … . Colors: `AppTheme.color.primary`, `.textColor`, `.surface`. Inspect `core/ui/src/main/kotlin/n7/ad2/core/ui/compose/` for the full palette.
- **Existing reusable composables** (in `core/ui`): `ErrorScreen(error, onRetryClicked)`, `LoadingAnimation(circleColor, animationDelay)`, `TextWithDotsSuffix`, `Popup`.
- **Logger is optional** — inject if needed, but most UI files don't.
- **Commit style:** kebab phrasing matching project history, e.g. `master: feat(hero-page/ui): …`. Match the `master: ` prefix used in prior commits.

---

## Context: Key Files

```
feature/hero-page/ui/
  build.gradle.kts                            ← modify (Task 1)
  src/main/kotlin/n7/ad2/hero/page/
    api/                                       ← untouched
    internal/
      di/HeroPageComponent.kt                  ← untouched
      pager/
        HeroPageFragment.kt                    ← rewrite (Task 6)
        HeroPageScreen.kt                      ← CREATE (Task 6)
        HeroPageToolbar.kt                     ← CREATE (Task 6)
        AnimatedToolbar.kt                     ← DELETE (Task 6)
      info/
        HeroInfoFragment.kt                    ← rewrite (Task 3)
        HeroInfoScreen.kt                      ← CREATE (Task 3)
        HeroInfoViewModel.kt                   ← untouched
        HeroStatistics.kt                      ← DELETE (Task 2)
        PopUpClickableSpan.kt                  ← untouched
        components/                            ← CREATE dir (Task 3)
          AttributesItem.kt, HeaderSoundItem.kt, SpellsRow.kt,
          SpellCard.kt, TalentItem.kt, BodyLineItem.kt,
          BodySimpleItem.kt, BodyWithImageItem.kt, BodyTalentItem.kt,
          HeaderItem.kt
        adapter/                               ← DELETE dir (Task 3)
        domain/adapter/                        ← DELETE dir (Task 3)
        domain/vo/VOHeroInfo.kt                ← modify (Task 2)
        domain/usecase/GetVOHeroDescriptionUseCase.kt  ← modify (Task 2)
      guides/
        HeroGuideFragment.kt                   ← rewrite (Task 4)
        HeroGuideScreen.kt                     ← CREATE (Task 4)
        HeroGuideViewModel.kt                  ← untouched
        HeroGuideTabAdapter.kt                 ← DELETE (Task 4)
        HeroGuideAdapter.kt                    ← DELETE (Task 4)
        HeroFlow.kt                            ← DELETE (Task 4)
        components/                            ← CREATE dir (Task 4)
          GuideTitleItem.kt, GuideInfoLineItem.kt,
          HeroChipsRow.kt, SpellBuildRow.kt,
          HeroItemsRow.kt, StartingItemsRow.kt,
          HeroChip.kt, SpellChip.kt, ItemChip.kt, StartingItemChip.kt
        domain/adapter/                        ← DELETE dir (Task 4)
        domain/vo/                             ← untouched (VO classes)
      responses/
        ResponsesFragment.kt                   ← rewrite (Task 5)
        ResponsesScreen.kt                     ← CREATE (Task 5)
        ResponsesViewModel.kt                  ← untouched
        DialogResponse.kt                      ← DELETE (Task 5)
        DownloadResponseManager.kt             ← untouched
        adapter/                               ← DELETE dir (Task 5)
        components/                            ← CREATE dir (Task 5)
          ResponseBodyItem.kt, ResponseImage.kt, ResponseDownloadDialog.kt
  src/main/res/layout/                         ← DELETE all 30 files (spread across Tasks 3-7)
```

**Keep unchanged:** All ViewModels, all `domain/usecase/`, all `domain/interactor/`, `DaggerHeroPageComponent`, `HeroPageProvider`, `HeroPageDependencies`, `HeroPageFragment.getInstance(heroName)` signature, `AudioExoPlayer`, `DownloadResponseManager`, `PopUpClickableSpan`, `HeroGuideWorker`, all `res/drawable/`, all `res/values/`.

---

## Task 1: Bootstrap build config

**Files:**
- Modify: `feature/hero-page/ui/build.gradle.kts`

**Context:** Currently the file has an uncommitted edit that removed `android { buildFeatures { dataBinding = false } }`. We'll keep that edit (no longer needed after XMLs are gone) and add Compose + `fragment-compose`.

- [ ] **Step 1: Rewrite `feature/hero-page/ui/build.gradle.kts`**

Replace the entire file with:

```kotlin
plugins {
    id("convention.android-library")
    id("convention.compose")
    id("convention.kotlin-serialization")
    id("n7.plugins.kotlin-ksp")
}

dependencies {
    implementation(libs.workManager)
    implementation(libs.jsoup)
    implementation(libs.ticker)

    implementation(libs.fragment.compose)

    implementation(projects.core.mediaPlayer)
    implementation(projects.core.commonAndroid)
    implementation(projects.core.dagger)
    implementation(projects.core.coroutines)
    implementation(projects.core.repositories)
    implementation(projects.core.logger.appLogger)
    implementation(projects.core.navigator)
    implementation(projects.core.spanParser)
    implementation(projects.core.mediaPlayer)
    implementation(projects.core.ui)

    implementation(projects.feature.heroes.domain.api)
    implementation(projects.feature.heroPage.domain.api)
    ksp(libs.daggerAnnotation)

    testImplementation(libs.test.junit.kotlin)
    testImplementation(libs.test.robolectric)
    testImplementation(libs.test.fragment)
    testImplementation(libs.test.runner)
    testImplementation(libs.test.espresso)

    androidTestImplementation(libs.test.junit.kotlin)
    androidTestImplementation(libs.test.fragment)
    androidTestImplementation(libs.test.runner)
    androidTestImplementation(libs.test.espresso)
    androidTestImplementation(libs.test.truth)
}
```

Changes vs. current file:
- Added `id("convention.compose")` plugin.
- Added `implementation(libs.fragment.compose)`.
- Removed the `android { buildFeatures { dataBinding = false } }` block (was uncommitted).
- Kept `libs.ticker` temporarily (deleted in Task 6 when `AnimatedToolbar` is replaced).

- [ ] **Step 2: Verify dependency resolution**

Run:
```
./gradlew :feature:hero-page:ui:dependencies --configuration debugCompileClasspath 2>&1 | grep -E "compose|fragment-compose" | head -10
```

Expected: output contains `androidx.fragment:fragment-compose` and `io.coil-kt:coil-compose`.

- [ ] **Step 3: Verify build is still broken (expected)**

Run:
```
./gradlew :feature:hero-page:ui:assembleDebug 2>&1 | tail -5
```

Expected: **BUILD FAILED** with the same ViewBinding errors as before. We haven't touched code yet. Commit anyway — this is a bootstrap step.

- [ ] **Step 4: Commit**

```bash
git add feature/hero-page/ui/build.gradle.kts
git commit -m "master: feat(hero-page/ui): add convention.compose and fragment-compose deps"
```

---

## Task 2: Move `HeroStatistics.Statistics` → `VOHeroStatistics`

**Files:**
- Modify: `feature/hero-page/ui/src/main/kotlin/n7/ad2/hero/page/internal/info/domain/vo/VOHeroInfo.kt`
- Modify: `feature/hero-page/ui/src/main/kotlin/n7/ad2/hero/page/internal/info/domain/usecase/GetVOHeroDescriptionUseCase.kt`

**Context:** `VOHeroInfo.Attributes` references a nested class `HeroStatistics.Statistics` declared inside the custom view we're deleting. Lifting it to a free-standing VO class unblocks deletion of `HeroStatistics.kt` in Task 3.

- [ ] **Step 1: Rewrite `VOHeroInfo.kt`**

Replace the file at `feature/hero-page/ui/src/main/kotlin/n7/ad2/hero/page/internal/info/domain/vo/VOHeroInfo.kt` with:

```kotlin
package n7.ad2.hero.page.internal.info.domain.vo

import android.text.SpannableString
import androidx.annotation.DrawableRes
import n7.ad2.ui.adapter.BodyViewHolder
import n7.ad2.ui.adapter.HeaderViewHolder

data class VOHeroStatistics(val strength: Double, val agility: Double, val intelligence: Double)

sealed class VOHeroInfo {
    data class HeaderSound(val title: String, val hotkey: String?, val legacyKey: String?, val isPlaying: Boolean, val soundUrl: String?) : VOHeroInfo()
    data class Header(val item: HeaderViewHolder.Data) : VOHeroInfo()
    data class Body(val item: BodyViewHolder.Data) : VOHeroInfo()
    data class Attributes(val urlHeroImage: String, val heroStatistics: VOHeroStatistics, val isSelected: Boolean) : VOHeroInfo()
    data class Spells(val spells: List<VOSpell>) : VOHeroInfo()
}

sealed class VOSpell {
    data class Simple(val name: String, val urlSpellImage: String, val isSelected: Boolean) : VOSpell()
    data class Talent(val name: String, val isSelected: Boolean) : VOSpell()
}

data class VOBodyTalent(val talentLeft: String, val talentLvl: String, val talentRight: String) : VOHeroInfo()
data class VOBodySimple(val body: String) : VOHeroInfo()
data class VOBodyWithImage(val body: SpannableString, @DrawableRes val drawable: Int, val tip: String) : VOHeroInfo()
data class VOBodyLine(val title: SpannableString) : VOHeroInfo()
```

Changes: removed `import n7.ad2.hero.page.internal.info.HeroStatistics`; added top-level `VOHeroStatistics`; changed `heroStatistics: HeroStatistics.Statistics` → `heroStatistics: VOHeroStatistics`.

- [ ] **Step 2: Update `GetVOHeroDescriptionUseCase.kt`**

Read the file:

```
Read: feature/hero-page/ui/src/main/kotlin/n7/ad2/hero/page/internal/info/domain/usecase/GetVOHeroDescriptionUseCase.kt
```

Find every usage of `HeroStatistics.Statistics` and replace with `VOHeroStatistics`. Also:
- Change `import n7.ad2.hero.page.internal.info.HeroStatistics` → `import n7.ad2.hero.page.internal.info.domain.vo.VOHeroStatistics`.
- Constructor calls `HeroStatistics.Statistics(strength, agility, intelligence)` → `VOHeroStatistics(strength, agility, intelligence)`.

- [ ] **Step 3: Find any other references and fix them**

Run:
```
grep -rn "HeroStatistics.Statistics" feature/hero-page/ui/src/main/kotlin
```

Expected: zero matches. If any match remains, replace with `VOHeroStatistics` and add import.

- [ ] **Step 4: Commit**

```bash
git add feature/hero-page/ui/src/main/kotlin/n7/ad2/hero/page/internal/info/
git commit -m "master: refactor(hero-page/ui): extract VOHeroStatistics from HeroStatistics view"
```

---

## Task 3: Migrate Hero Info tab to Compose

**Files:**
- Create: `feature/hero-page/ui/src/main/kotlin/n7/ad2/hero/page/internal/info/HeroInfoScreen.kt`
- Create: `feature/hero-page/ui/src/main/kotlin/n7/ad2/hero/page/internal/info/components/` (10 files)
- Rewrite: `feature/hero-page/ui/src/main/kotlin/n7/ad2/hero/page/internal/info/HeroInfoFragment.kt`
- Delete: `feature/hero-page/ui/src/main/kotlin/n7/ad2/hero/page/internal/info/HeroStatistics.kt`
- Delete: `feature/hero-page/ui/src/main/kotlin/n7/ad2/hero/page/internal/info/adapter/` (8 files)
- Delete: `feature/hero-page/ui/src/main/kotlin/n7/ad2/hero/page/internal/info/domain/adapter/HeroHeaderPlayableViewHolder.kt` (and remove empty dir)
- Delete: 13 XML files in `feature/hero-page/ui/src/main/res/layout/` (see Step 12)

- [ ] **Step 1: Create `components/AttributesItem.kt`**

```kotlin
package n7.ad2.hero.page.internal.info.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import n7.ad2.core.ui.compose.AppTheme
import n7.ad2.feature.hero.page.ui.R
import n7.ad2.hero.page.internal.info.domain.vo.VOHeroInfo

@Composable
internal fun AttributesItem(item: VOHeroInfo.Attributes, onClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        AsyncImage(
            model = item.urlHeroImage,
            contentDescription = null,
            modifier = Modifier.size(72.dp),
        )
        StatColumn(R.drawable.strength, item.heroStatistics.strength.toString())
        StatColumn(R.drawable.agility, item.heroStatistics.agility.toString())
        StatColumn(R.drawable.intelligence, item.heroStatistics.intelligence.toString())
    }
}

@Composable
private fun StatColumn(@androidx.annotation.DrawableRes icon: Int, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Image(painter = painterResource(icon), contentDescription = null, modifier = Modifier.size(24.dp))
        Text(text = value, style = AppTheme.style.body, color = AppTheme.color.textColor)
    }
}
```

- [ ] **Step 2: Create `components/HeaderSoundItem.kt`**

```kotlin
package n7.ad2.hero.page.internal.info.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import n7.ad2.core.ui.compose.AppTheme
import n7.ad2.hero.page.internal.info.domain.vo.VOHeroInfo

@Composable
internal fun HeaderSoundItem(item: VOHeroInfo.HeaderSound, onPlayClick: (String) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(text = item.title, style = AppTheme.style.H6, color = AppTheme.color.textColor, modifier = Modifier.weight(1f))
        item.hotkey?.let { Text(text = it, style = AppTheme.style.body, color = AppTheme.color.textColor) }
        item.soundUrl?.let { url ->
            IconButton(onClick = { onPlayClick(url) }) {
                Icon(
                    imageVector = if (item.isPlaying) Icons.Default.Stop else Icons.Default.PlayArrow,
                    contentDescription = null,
                    tint = AppTheme.color.primary,
                )
            }
        }
    }
}
```

- [ ] **Step 3: Create `components/SpellsRow.kt` and `components/SpellCard.kt`**

`SpellsRow.kt`:

```kotlin
package n7.ad2.hero.page.internal.info.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import n7.ad2.hero.page.internal.info.domain.vo.VOHeroInfo
import n7.ad2.hero.page.internal.info.domain.vo.VOSpell

@Composable
internal fun SpellsRow(
    item: VOHeroInfo.Spells,
    onSpellClick: (VOSpell.Simple) -> Unit,
    onTalentClick: (VOSpell.Talent) -> Unit,
) {
    LazyRow(
        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        modifier = Modifier.padding(vertical = 4.dp),
    ) {
        items(items = item.spells, key = { it }) { spell ->
            when (spell) {
                is VOSpell.Simple -> SpellCard(spell, onClick = { onSpellClick(spell) })
                is VOSpell.Talent -> TalentCard(spell, onClick = { onTalentClick(spell) })
            }
        }
    }
}
```

`SpellCard.kt`:

```kotlin
package n7.ad2.hero.page.internal.info.components

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import n7.ad2.core.ui.compose.AppTheme
import n7.ad2.hero.page.internal.info.domain.vo.VOSpell

@Composable
internal fun SpellCard(spell: VOSpell.Simple, onClick: () -> Unit) {
    val shape = RoundedCornerShape(6.dp)
    val border = if (spell.isSelected) 2.dp else 0.dp
    AsyncImage(
        model = spell.urlSpellImage,
        contentDescription = spell.name,
        modifier = Modifier
            .size(48.dp)
            .clip(shape)
            .border(border, AppTheme.color.primary, shape)
            .clickable { onClick() },
    )
}

@Composable
internal fun TalentCard(spell: VOSpell.Talent, onClick: () -> Unit) {
    androidx.compose.material.Text(
        text = spell.name,
        modifier = Modifier.clickable { onClick() }.size(48.dp),
        color = if (spell.isSelected) AppTheme.color.primary else AppTheme.color.textColor,
    )
}
```

- [ ] **Step 4: Create `components/TalentItem.kt`** (for `VOBodyTalent`, not talent cards)

```kotlin
package n7.ad2.hero.page.internal.info.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import n7.ad2.core.ui.compose.AppTheme
import n7.ad2.hero.page.internal.info.domain.vo.VOBodyTalent

@Composable
internal fun TalentItem(item: VOBodyTalent) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(text = item.talentLeft, style = AppTheme.style.body, color = AppTheme.color.textColor, modifier = Modifier.weight(1f), textAlign = TextAlign.End)
        Text(text = item.talentLvl, style = AppTheme.style.H6, color = AppTheme.color.primary, modifier = Modifier.padding(horizontal = 8.dp))
        Text(text = item.talentRight, style = AppTheme.style.body, color = AppTheme.color.textColor, modifier = Modifier.weight(1f), textAlign = TextAlign.Start)
    }
}
```

- [ ] **Step 5: Create `components/BodyLineItem.kt`, `BodySimpleItem.kt`, `BodyWithImageItem.kt`, `BodyTalentItem.kt`**

`BodySimpleItem.kt`:

```kotlin
package n7.ad2.hero.page.internal.info.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import n7.ad2.core.ui.compose.AppTheme
import n7.ad2.hero.page.internal.info.domain.vo.VOBodySimple

@Composable
internal fun BodySimpleItem(item: VOBodySimple) {
    Text(
        text = item.body,
        style = AppTheme.style.body,
        color = AppTheme.color.textColor,
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp),
    )
}
```

`BodyLineItem.kt` — uses `AndroidView` because body is `SpannableString` with `ImageSpan`:

```kotlin
package n7.ad2.hero.page.internal.info.components

import android.view.Gravity
import android.widget.TextView
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import n7.ad2.hero.page.internal.info.domain.vo.VOBodyLine

@Composable
internal fun BodyLineItem(item: VOBodyLine) {
    AndroidView(
        factory = { context ->
            TextView(context).apply { gravity = Gravity.CENTER_VERTICAL }
        },
        update = { view -> view.text = item.title },
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 2.dp),
    )
}
```

`BodyWithImageItem.kt` — same pattern, also wraps `TextView` since `body: SpannableString` has `ImageSpan`s:

```kotlin
package n7.ad2.hero.page.internal.info.components

import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import n7.ad2.hero.page.internal.info.domain.vo.VOBodyWithImage

@Composable
internal fun BodyWithImageItem(item: VOBodyWithImage) {
    AndroidView(
        factory = { ctx ->
            LinearLayout(ctx).apply {
                orientation = LinearLayout.HORIZONTAL
                addView(ImageView(ctx).apply { setImageResource(item.drawable) })
                addView(TextView(ctx))
            }
        },
        update = { row ->
            (row.getChildAt(1) as TextView).text = item.body
        },
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp),
    )
}
```

`BodyTalentItem.kt` — for `VOHeroInfo.Body`:

```kotlin
package n7.ad2.hero.page.internal.info.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import n7.ad2.core.ui.compose.AppTheme
import n7.ad2.ui.adapter.BodyViewHolder

@Composable
internal fun BodyItem(data: BodyViewHolder.Data) {
    Text(
        text = data.toString(),   // BodyViewHolder.Data has simple String content; inspect class if needed
        style = AppTheme.style.body,
        color = AppTheme.color.textColor,
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp),
    )
}
```

**Note:** Before committing Task 3, read `core/ui/src/main/kotlin/n7/ad2/ui/adapter/BodyViewHolder.kt` to see what fields `BodyViewHolder.Data` has. Replace `data.toString()` with the real field access (likely `data.title` or `data.body`).

- [ ] **Step 6: Create `components/HeaderItem.kt`**

```kotlin
package n7.ad2.hero.page.internal.info.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import n7.ad2.core.ui.compose.AppTheme
import n7.ad2.ui.adapter.HeaderViewHolder

@Composable
internal fun HeaderItem(data: HeaderViewHolder.Data) {
    Text(
        text = data.title,
        style = AppTheme.style.H5,
        color = AppTheme.color.primary,
        modifier = Modifier
            .fillMaxWidth()
            .background(AppTheme.color.surface)
            .padding(horizontal = 16.dp, vertical = 8.dp),
    )
}
```

- [ ] **Step 7: Create `HeroInfoScreen.kt`**

```kotlin
package n7.ad2.hero.page.internal.info

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import n7.ad2.core.ui.compose.view.ErrorScreen
import n7.ad2.core.ui.compose.view.LoadingAnimation
import n7.ad2.hero.page.internal.info.components.AttributesItem
import n7.ad2.hero.page.internal.info.components.BodyItem
import n7.ad2.hero.page.internal.info.components.BodyLineItem
import n7.ad2.hero.page.internal.info.components.BodySimpleItem
import n7.ad2.hero.page.internal.info.components.BodyWithImageItem
import n7.ad2.hero.page.internal.info.components.HeaderItem
import n7.ad2.hero.page.internal.info.components.HeaderSoundItem
import n7.ad2.hero.page.internal.info.components.SpellsRow
import n7.ad2.hero.page.internal.info.components.TalentItem
import n7.ad2.hero.page.internal.info.domain.usecase.GetVOHeroDescriptionUseCase
import n7.ad2.hero.page.internal.info.domain.vo.VOBodyLine
import n7.ad2.hero.page.internal.info.domain.vo.VOBodySimple
import n7.ad2.hero.page.internal.info.domain.vo.VOBodyTalent
import n7.ad2.hero.page.internal.info.domain.vo.VOBodyWithImage
import n7.ad2.hero.page.internal.info.domain.vo.VOHeroInfo
import n7.ad2.hero.page.internal.info.domain.vo.VOSpell

@OptIn(androidx.compose.foundation.ExperimentalFoundationApi::class)
@Composable
internal fun HeroInfoScreen(viewModel: HeroInfoViewModel) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    when (val s = state) {
        HeroInfoViewModel.State.Loading -> LoadingAnimation()
        is HeroInfoViewModel.State.Error -> ErrorScreen(error = s.error)
        is HeroInfoViewModel.State.Data -> LazyColumn(
            contentPadding = PaddingValues(bottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()),
        ) {
            s.list.forEach { vo ->
                when (vo) {
                    is VOHeroInfo.Header -> stickyHeader(key = vo) { HeaderItem(vo.item) }
                    is VOHeroInfo.HeaderSound -> item(key = vo) {
                        HeaderSoundItem(item = vo, onPlayClick = { /* audio wiring handled in Task 5/6 via callback */ })
                    }
                    is VOHeroInfo.Attributes -> item(key = vo) {
                        AttributesItem(item = vo, onClick = { viewModel.load(GetVOHeroDescriptionUseCase.HeroInfo.Main) })
                    }
                    is VOHeroInfo.Body -> item(key = vo) { BodyItem(vo.item) }
                    is VOHeroInfo.Spells -> item(key = vo) {
                        SpellsRow(
                            item = vo,
                            onSpellClick = { viewModel.load(GetVOHeroDescriptionUseCase.HeroInfo.Spell(it.name)) },
                            onTalentClick = { viewModel.load(GetVOHeroDescriptionUseCase.HeroInfo.Spell(it.name)) },
                        )
                    }
                    is VOBodyLine -> item(key = vo) { BodyLineItem(vo) }
                    is VOBodySimple -> item(key = vo) { BodySimpleItem(vo) }
                    is VOBodyWithImage -> item(key = vo) { BodyWithImageItem(vo) }
                    is VOBodyTalent -> item(key = vo) { TalentItem(vo) }
                }
            }
        }
    }
}
```

- [ ] **Step 8: Rewrite `HeroInfoFragment.kt`**

```kotlin
package n7.ad2.hero.page.internal.info

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import n7.ad2.android.findDependencies
import n7.ad2.core.ui.content
import n7.ad2.hero.page.internal.di.DaggerHeroPageComponent
import n7.ad2.ktx.lazyUnsafe
import n7.ad2.ktx.viewModel
import javax.inject.Inject

class HeroInfoFragment : Fragment() {

    companion object {
        private const val HERO_NAME = "HERO_NAME"
        fun newInstance(heroName: String): HeroInfoFragment = HeroInfoFragment().apply {
            arguments = bundleOf(HERO_NAME to heroName)
        }
    }

    @Inject lateinit var heroInfoFactory: HeroInfoViewModel.Factory
    private val heroName by lazyUnsafe { requireArguments().getString(HERO_NAME)!! }
    private val viewModel: HeroInfoViewModel by viewModel { heroInfoFactory.create(heroName) }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        DaggerHeroPageComponent.factory().create(findDependencies()).inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
        content { HeroInfoScreen(viewModel) }
}
```

- [ ] **Step 9: Delete old Hero Info Kotlin files**

```bash
rm feature/hero-page/ui/src/main/kotlin/n7/ad2/hero/page/internal/info/HeroStatistics.kt
rm -r feature/hero-page/ui/src/main/kotlin/n7/ad2/hero/page/internal/info/adapter/
rm -r feature/hero-page/ui/src/main/kotlin/n7/ad2/hero/page/internal/info/domain/adapter/
```

- [ ] **Step 10: Delete old Hero Info XML files**

```bash
rm feature/hero-page/ui/src/main/res/layout/fragment_hero_info.xml
rm feature/hero-page/ui/src/main/res/layout/hero_statistics.xml
rm feature/hero-page/ui/src/main/res/layout/item_body_line.xml
rm feature/hero-page/ui/src/main/res/layout/item_body_simple.xml
rm feature/hero-page/ui/src/main/res/layout/item_body_talent.xml
rm feature/hero-page/ui/src/main/res/layout/item_body_with_image.xml
rm feature/hero-page/ui/src/main/res/layout/item_hero_attributes.xml
rm feature/hero-page/ui/src/main/res/layout/item_hero_header_playable.xml
rm feature/hero-page/ui/src/main/res/layout/item_hero_spells.xml
rm feature/hero-page/ui/src/main/res/layout/item_spell.xml
rm feature/hero-page/ui/src/main/res/layout/item_talent.xml
```

- [ ] **Step 11: Build verification**

Run:
```
./gradlew :feature:hero-page:ui:compileDebugKotlin 2>&1 | tail -30
```

Expected outcomes (whichever comes first, fix it):
- **BUILD FAILED** with references to still-existing adapter code that depends on deleted classes → read errors, fix imports, remove leftover usages. Most likely remaining usage is in `HeroGuideFragment`, `HeroPageFragment`, or `ResponsesFragment` that still have `FragmentXxxBinding` imports — those fragments will be rewritten in Tasks 4-6; for now comment out their bodies or make them return empty `FrameLayout(context)` views so this task compiles:

  If `HeroGuideFragment.kt`, `HeroPageFragment.kt`, or `ResponsesFragment.kt` fail to compile because of still-referenced `FragmentXxxBinding`, replace their entire body **temporarily** with:

  ```kotlin
  class HeroGuideFragment : Fragment() {
      override fun onCreateView(i: LayoutInflater, c: ViewGroup?, s: Bundle?) = FrameLayout(requireContext())
      companion object { fun newInstance(heroName: String) = HeroGuideFragment() }
  }
  ```

  This is temporary scaffolding until Tasks 4-6 rewrite them properly.

- **BUILD SUCCESSFUL** → proceed to commit.

- [ ] **Step 12: Commit**

```bash
git add feature/hero-page/ui/
git commit -m "master: feat(hero-page/ui): migrate Hero Info tab to Compose"
```

---

## Task 4: Migrate Hero Guide tab to Compose

**Files:**
- Create: `feature/hero-page/ui/src/main/kotlin/n7/ad2/hero/page/internal/guides/HeroGuideScreen.kt`
- Create: `feature/hero-page/ui/src/main/kotlin/n7/ad2/hero/page/internal/guides/components/` (10 files)
- Rewrite: `feature/hero-page/ui/src/main/kotlin/n7/ad2/hero/page/internal/guides/HeroGuideFragment.kt`
- Delete: `HeroFlow.kt`, `HeroGuideAdapter.kt`, `HeroGuideTabAdapter.kt`
- Delete: `guides/domain/adapter/HeroGuideAdapter.kt` and empty dir
- Delete: 8 XML files

**Context:** `VOHeroFlow*` data classes live in `HeroFlow.kt`. Before deleting the file, move the 4 data classes to a new file `guides/domain/vo/VOHeroFlowItems.kt`.

- [ ] **Step 1: Move `VOHeroFlow*` data classes out of `HeroFlow.kt`**

Create `feature/hero-page/ui/src/main/kotlin/n7/ad2/hero/page/internal/guides/domain/vo/VOHeroFlowItems.kt`:

```kotlin
package n7.ad2.hero.page.internal.guides.domain.vo

data class VOHeroFlowItem(val heroName: String, val urlHeroImage: String, val heroWinrate: String)
data class VOHeroFlowSpell(val skillName: String, val urlImageSkill: String, val skillOrder: String)
data class VOHeroFlowStartingHeroItem(val itemName: String, val urlHeroItem: String)
data class VOHeroFlowHeroItem(val itemName: String, val urlHeroItem: String, val itemTiming: String?)
```

Update imports in `VOGuideItem.kt`:
- Change `import n7.ad2.hero.page.internal.guides.VOHeroFlow*` → `import n7.ad2.hero.page.internal.guides.domain.vo.VOHeroFlow*` (4 imports).

Update imports anywhere else they are referenced:
```
grep -rn "n7.ad2.hero.page.internal.guides.VOHeroFlow" feature/hero-page/ui/src/main/kotlin
```

Replace each match with `n7.ad2.hero.page.internal.guides.domain.vo.VOHeroFlow*`.

- [ ] **Step 2: Create `components/chips/HeroChip.kt`**

```kotlin
package n7.ad2.hero.page.internal.guides.components.chips

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import n7.ad2.core.ui.compose.AppTheme
import n7.ad2.hero.page.internal.guides.domain.vo.VOHeroFlowItem

@Composable
internal fun HeroChip(item: VOHeroFlowItem, good: Boolean) {
    Column(
        modifier = Modifier.padding(2.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        AsyncImage(
            model = item.urlHeroImage,
            contentDescription = item.heroName,
            modifier = Modifier.size(48.dp).clip(RoundedCornerShape(4.dp)),
        )
        Text(
            text = item.heroWinrate,
            style = AppTheme.style.body,
            color = if (good) Color(0xFF4CAF50) else Color(0xFFF44336),
        )
    }
}
```

- [ ] **Step 3: Create `components/chips/SpellChip.kt`**

```kotlin
package n7.ad2.hero.page.internal.guides.components.chips

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import n7.ad2.hero.page.internal.guides.domain.vo.VOHeroFlowSpell

@Composable
internal fun SpellChip(item: VOHeroFlowSpell) {
    Box(modifier = Modifier.padding(2.dp)) {
        AsyncImage(
            model = item.urlImageSkill,
            contentDescription = item.skillName,
            modifier = Modifier.size(40.dp).clip(RoundedCornerShape(4.dp)),
        )
        Text(
            text = item.skillOrder,
            color = Color.White,
            modifier = Modifier.align(Alignment.BottomEnd).padding(2.dp),
        )
    }
}
```

- [ ] **Step 4: Create `components/chips/ItemChip.kt` and `StartingItemChip.kt`**

`ItemChip.kt`:

```kotlin
package n7.ad2.hero.page.internal.guides.components.chips

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import n7.ad2.core.ui.compose.AppTheme
import n7.ad2.feature.hero.page.ui.R
import n7.ad2.hero.page.internal.guides.domain.vo.VOHeroFlowHeroItem

@Composable
internal fun ItemChip(item: VOHeroFlowHeroItem) {
    Row(
        modifier = Modifier.padding(2.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        AsyncImage(
            model = item.urlHeroItem,
            contentDescription = item.itemName,
            modifier = Modifier.size(32.dp).clip(RoundedCornerShape(4.dp)),
        )
        item.itemTiming?.let {
            Image(painter = painterResource(R.drawable.ic_arrow_right), contentDescription = null)
            Text(text = it, style = AppTheme.style.body, color = AppTheme.color.textColor)
        }
    }
}
```

`StartingItemChip.kt`:

```kotlin
package n7.ad2.hero.page.internal.guides.components.chips

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import n7.ad2.hero.page.internal.guides.domain.vo.VOHeroFlowStartingHeroItem

@Composable
internal fun StartingItemChip(item: VOHeroFlowStartingHeroItem) {
    AsyncImage(
        model = item.urlHeroItem,
        contentDescription = item.itemName,
        modifier = Modifier.size(40.dp).padding(2.dp).clip(RoundedCornerShape(4.dp)),
    )
}
```

- [ ] **Step 5: Create `components/GuideTitleItem.kt`, `GuideInfoLineItem.kt`**

`GuideTitleItem.kt`:

```kotlin
package n7.ad2.hero.page.internal.guides.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import n7.ad2.core.ui.compose.AppTheme

@Composable
internal fun GuideTitleItem(title: String) {
    Text(
        text = title,
        style = AppTheme.style.H5,
        color = AppTheme.color.primary,
        modifier = Modifier.fillMaxWidth().background(AppTheme.color.surface).padding(16.dp),
    )
}
```

`GuideInfoLineItem.kt`:

```kotlin
package n7.ad2.hero.page.internal.guides.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import n7.ad2.core.ui.compose.AppTheme

@Composable
internal fun GuideInfoLineItem(title: String) {
    Text(
        text = title,
        style = AppTheme.style.body,
        color = AppTheme.color.textColor,
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp),
    )
}
```

- [ ] **Step 6: Create `HeroGuideScreen.kt`**

```kotlin
package n7.ad2.hero.page.internal.guides

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import n7.ad2.hero.page.internal.guides.components.GuideInfoLineItem
import n7.ad2.hero.page.internal.guides.components.GuideTitleItem
import n7.ad2.hero.page.internal.guides.components.chips.HeroChip
import n7.ad2.hero.page.internal.guides.components.chips.ItemChip
import n7.ad2.hero.page.internal.guides.components.chips.SpellChip
import n7.ad2.hero.page.internal.guides.components.chips.StartingItemChip
import n7.ad2.hero.page.internal.guides.domain.vo.VOGuideEasyToWinHeroes
import n7.ad2.hero.page.internal.guides.domain.vo.VOGuideHardToWinHeroes
import n7.ad2.hero.page.internal.guides.domain.vo.VOGuideHeroItems
import n7.ad2.hero.page.internal.guides.domain.vo.VOGuideInfoLine
import n7.ad2.hero.page.internal.guides.domain.vo.VOGuideItem
import n7.ad2.hero.page.internal.guides.domain.vo.VOGuideSpellBuild
import n7.ad2.hero.page.internal.guides.domain.vo.VOGuideStartingHeroItems
import n7.ad2.hero.page.internal.guides.domain.vo.VOGuideTitle

@OptIn(ExperimentalLayoutApi::class, androidx.compose.foundation.ExperimentalFoundationApi::class)
@Composable
internal fun HeroGuideScreen(viewModel: HeroGuideViewModel, heroName: String) {
    val items by viewModel.loadHeroWithGuides(heroName).observeAsState(emptyList())
    LazyColumn {
        items.forEach { item ->
            when (item) {
                is VOGuideTitle -> stickyHeader(key = item) { GuideTitleItem(item.title) }
                is VOGuideInfoLine -> item(key = item) { GuideInfoLineItem(item.title) }
                is VOGuideEasyToWinHeroes -> item(key = item) {
                    FlowRow(Modifier.fillMaxWidth().padding(4.dp), horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                        item.list.forEach { HeroChip(it, good = true) }
                    }
                }
                is VOGuideHardToWinHeroes -> item(key = item) {
                    FlowRow(Modifier.fillMaxWidth().padding(4.dp), horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                        item.list.forEach { HeroChip(it, good = false) }
                    }
                }
                is VOGuideSpellBuild -> item(key = item) {
                    FlowRow(Modifier.fillMaxWidth().padding(4.dp)) { item.list.forEach { SpellChip(it) } }
                }
                is VOGuideStartingHeroItems -> item(key = item) {
                    FlowRow(Modifier.fillMaxWidth().padding(4.dp)) { item.list.forEach { StartingItemChip(it) } }
                }
                is VOGuideHeroItems -> item(key = item) {
                    FlowRow(Modifier.fillMaxWidth().padding(4.dp)) { item.list.forEach { ItemChip(it) } }
                }
            }
        }
    }
}
```

- [ ] **Step 7: Rewrite `HeroGuideFragment.kt`**

```kotlin
package n7.ad2.hero.page.internal.guides

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import n7.ad2.android.findDependencies
import n7.ad2.core.ui.content
import n7.ad2.hero.page.internal.di.DaggerHeroPageComponent
import n7.ad2.ktx.lazyUnsafe
import n7.ad2.ktx.viewModel
import javax.inject.Inject

class HeroGuideFragment : Fragment() {

    companion object {
        private const val HERO_NAME = "HERO_NAME"
        fun newInstance(heroName: String): HeroGuideFragment = HeroGuideFragment().apply {
            arguments = bundleOf(HERO_NAME to heroName)
        }
    }

    @Inject lateinit var heroesViewModelFactory: HeroGuideViewModel.Factory
    private val heroName by lazyUnsafe { requireArguments().getString(HERO_NAME)!! }
    private val viewModel: HeroGuideViewModel by viewModel { heroesViewModelFactory.create() }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        DaggerHeroPageComponent.factory().create(findDependencies()).inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
        content { HeroGuideScreen(viewModel, heroName) }
}
```

- [ ] **Step 8: Delete old Hero Guide Kotlin files**

```bash
rm feature/hero-page/ui/src/main/kotlin/n7/ad2/hero/page/internal/guides/HeroFlow.kt
rm feature/hero-page/ui/src/main/kotlin/n7/ad2/hero/page/internal/guides/HeroGuideAdapter.kt
rm feature/hero-page/ui/src/main/kotlin/n7/ad2/hero/page/internal/guides/HeroGuideTabAdapter.kt
rm -r feature/hero-page/ui/src/main/kotlin/n7/ad2/hero/page/internal/guides/domain/adapter/
```

- [ ] **Step 9: Delete old Hero Guide XML files**

```bash
rm feature/hero-page/ui/src/main/res/layout/fragment_hero_guide.xml
rm feature/hero-page/ui/src/main/res/layout/flow_hero.xml
rm feature/hero-page/ui/src/main/res/layout/flow_hero_item.xml
rm feature/hero-page/ui/src/main/res/layout/flow_spell.xml
rm feature/hero-page/ui/src/main/res/layout/flow_starting_hero_item.xml
rm feature/hero-page/ui/src/main/res/layout/item_easy_to_win_heroes.xml
rm feature/hero-page/ui/src/main/res/layout/item_guide_hero_items.xml
rm feature/hero-page/ui/src/main/res/layout/item_guide_info_line.xml
rm feature/hero-page/ui/src/main/res/layout/item_guide_skill_build.xml
rm feature/hero-page/ui/src/main/res/layout/item_guide_starting_hero_items.xml
rm feature/hero-page/ui/src/main/res/layout/item_guide_title.xml
rm feature/hero-page/ui/src/main/res/layout/item_hard_to_win_heroes.xml
rm feature/hero-page/ui/src/main/res/layout/item_hero_guide_tab_number.xml
```

- [ ] **Step 10: Build verification**

```
./gradlew :feature:hero-page:ui:compileDebugKotlin 2>&1 | tail -30
```

Expected: BUILD SUCCESSFUL, with `HeroPageFragment` and `ResponsesFragment` stubbed to `FrameLayout(context)` (as noted in Task 3 Step 11). If build fails, read the error; likely a missing `composeLivedata` import (`import androidx.compose.runtime.livedata.observeAsState`) — that comes from the Compose bundle.

- [ ] **Step 11: Commit**

```bash
git add feature/hero-page/ui/
git commit -m "master: feat(hero-page/ui): migrate Hero Guide tab to Compose"
```

---

## Task 5: Migrate Responses tab to Compose

**Files:**
- Create: `feature/hero-page/ui/src/main/kotlin/n7/ad2/hero/page/internal/responses/ResponsesScreen.kt`
- Create: `feature/hero-page/ui/src/main/kotlin/n7/ad2/hero/page/internal/responses/components/` (3 files)
- Rewrite: `feature/hero-page/ui/src/main/kotlin/n7/ad2/hero/page/internal/responses/ResponsesFragment.kt`
- Delete: `DialogResponse.kt`, `responses/adapter/` dir (5 files)
- Delete: 4 XML files

- [ ] **Step 1: Create `components/ResponseImage.kt`**

```kotlin
package n7.ad2.hero.page.internal.responses.components

import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import n7.ad2.hero.page.internal.responses.domain.vo.VOResponseImage

@Composable
internal fun ResponseImage(image: VOResponseImage) {
    AsyncImage(
        model = image.imageUrl,
        contentDescription = image.heroName,
        modifier = Modifier.size(24.dp).clip(CircleShape),
    )
}
```

- [ ] **Step 2: Create `components/ResponseBodyItem.kt`**

```kotlin
package n7.ad2.hero.page.internal.responses.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import n7.ad2.core.ui.compose.AppTheme
import n7.ad2.hero.page.internal.responses.domain.vo.VOResponse

@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun ResponseBodyItem(
    item: VOResponse.Body,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(onClick = onClick, onLongClick = onLongClick)
            .background(if (item.isSavedInMemory) AppTheme.color.primary.copy(alpha = 0.1f) else Color.Transparent)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = item.title,
            style = AppTheme.style.body,
            color = AppTheme.color.textColor,
            modifier = Modifier.weight(1f),
        )
        if (item.icons.isNotEmpty()) {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                items(items = item.icons, key = { it.heroName + it.imageUrl }) { ResponseImage(it) }
            }
        }
    }
}
```

- [ ] **Step 3: Create `components/ResponseDownloadDialog.kt`**

```kotlin
package n7.ad2.hero.page.internal.responses.components

import androidx.compose.material.AlertDialog
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import n7.ad2.feature.hero.page.ui.R

@Composable
internal fun ResponseDownloadDialog(onConfirm: () -> Unit, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = stringResource(R.string.response_download_title)) },
        text = { Text(text = stringResource(R.string.response_download_message)) },
        confirmButton = { TextButton(onClick = onConfirm) { Text(stringResource(R.string.response_download_confirm)) } },
        dismissButton = { TextButton(onClick = onDismiss) { Text(stringResource(android.R.string.cancel)) } },
    )
}
```

**Note:** The three string resources (`response_download_title`, `response_download_message`, `response_download_confirm`) may not exist. Check `res/values/strings.xml` — if missing, add them (read the deleted `dialog_response.xml` for prior labels, or use placeholder strings for now). If keeping it simple: replace all three with literal strings `"Download response?"`, `"Save this audio to your device"`, `"Download"` and skip the resources.

- [ ] **Step 4: Create `ResponsesScreen.kt`**

```kotlin
package n7.ad2.hero.page.internal.responses

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import n7.ad2.core.ui.compose.view.ErrorScreen
import n7.ad2.hero.page.internal.responses.components.ResponseBodyItem
import n7.ad2.hero.page.internal.responses.components.ResponseDownloadDialog
import n7.ad2.hero.page.internal.info.components.HeaderItem
import n7.ad2.hero.page.internal.responses.domain.vo.VOResponse

@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun ResponsesScreen(
    viewModel: ResponsesViewModel,
    onPlay: (VOResponse.Body) -> Unit,
    onDownload: (VOResponse.Body) -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    var dialogTarget by remember { mutableStateOf<VOResponse.Body?>(null) }

    when (val s = state) {
        ResponsesViewModel.State.Loading -> Unit
        is ResponsesViewModel.State.Error -> ErrorScreen(error = s.error)
        is ResponsesViewModel.State.Data -> LazyColumn {
            s.list.forEach { r ->
                when (r) {
                    is VOResponse.Title -> stickyHeader(key = r) { HeaderItem(r.data) }
                    is VOResponse.Body -> item(key = r.title) {
                        ResponseBodyItem(
                            item = r,
                            onClick = { onPlay(r) },
                            onLongClick = { if (!r.isSavedInMemory) dialogTarget = r },
                        )
                    }
                }
            }
        }
    }

    dialogTarget?.let { body ->
        ResponseDownloadDialog(
            onConfirm = { onDownload(body); dialogTarget = null },
            onDismiss = { dialogTarget = null },
        )
    }
}
```

- [ ] **Step 5: Rewrite `ResponsesFragment.kt`**

```kotlin
package n7.ad2.hero.page.internal.responses

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import n7.ad2.android.findDependencies
import n7.ad2.core.ui.content
import n7.ad2.hero.page.internal.di.DaggerHeroPageComponent
import n7.ad2.hero.page.internal.responses.domain.vo.VOResponse
import n7.ad2.ktx.lazyUnsafe
import n7.ad2.ktx.viewModel
import n7.ad2.mediaplayer.AudioExoPlayer
import n7.ad2.ui.showDialogError
import javax.inject.Inject

class ResponsesFragment : Fragment() {

    companion object {
        private const val HERO_NAME = "HERO_NAME"
        fun newInstance(heroName: String): ResponsesFragment = ResponsesFragment().apply {
            arguments = bundleOf(HERO_NAME to heroName)
        }
    }

    @Inject lateinit var responsesViewModelFactory: ResponsesViewModel.Factory
    @Inject lateinit var audioExoPlayerFactory: AudioExoPlayer.Factory

    private val heroName by lazyUnsafe { requireArguments().getString(HERO_NAME)!! }
    private val viewModel: ResponsesViewModel by viewModel { responsesViewModelFactory.create(heroName) }
    private val audioExoPlayer by lazyUnsafe { audioExoPlayerFactory.create(lifecycle) }
    private val downloadResponseManager by lazyUnsafe {
        DownloadResponseManager(requireActivity().contentResolver, requireActivity().application, lifecycle)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        DaggerHeroPageComponent.factory().create(findDependencies()).inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        downloadResponseManager.setDownloadListener { result ->
            when (result) {
                is DownloadResult.Success -> viewModel.refreshResponses()
                is DownloadResult.Failed -> showDialogError(result.error)
                is DownloadResult.InProgress -> Unit   // progress indication not yet wired into Composable
            }
        }
        audioExoPlayer.playerStateListener = { state ->
            when (state) {
                AudioExoPlayer.PlayerState.Ended -> Unit
                is AudioExoPlayer.PlayerState.Error -> showDialogError(state.error)
            }
        }
        viewModel.error
            .flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
            .onEach { /* errors surfaced via ErrorScreen in composable */ }
            .launchIn(lifecycleScope)

        return content {
            ResponsesScreen(
                viewModel = viewModel,
                onPlay = { audioExoPlayer.play(it.audioUrl) },
                onDownload = { body ->
                    val id = downloadResponseManager.download(body)
                    body.downloadID = id
                },
            )
        }
    }
}
```

- [ ] **Step 6: Delete old Responses Kotlin files**

```bash
rm feature/hero-page/ui/src/main/kotlin/n7/ad2/hero/page/internal/responses/DialogResponse.kt
rm -r feature/hero-page/ui/src/main/kotlin/n7/ad2/hero/page/internal/responses/adapter/
```

- [ ] **Step 7: Delete old Responses XML files**

```bash
rm feature/hero-page/ui/src/main/res/layout/fragment_hero_responses.xml
rm feature/hero-page/ui/src/main/res/layout/dialog_response.xml
rm feature/hero-page/ui/src/main/res/layout/item_response_body.xml
rm feature/hero-page/ui/src/main/res/layout/item_response_image.xml
```

- [ ] **Step 8: Build verification**

```
./gradlew :feature:hero-page:ui:compileDebugKotlin 2>&1 | tail -30
```

Expected: BUILD SUCCESSFUL. `HeroPageFragment` is still stubbed to `FrameLayout` — Task 6 fixes it.

- [ ] **Step 9: Commit**

```bash
git add feature/hero-page/ui/
git commit -m "master: feat(hero-page/ui): migrate Responses tab to Compose"
```

---

## Task 6: Migrate Hero Page pager shell to Compose

**Files:**
- Create: `feature/hero-page/ui/src/main/kotlin/n7/ad2/hero/page/internal/pager/HeroPageScreen.kt`
- Create: `feature/hero-page/ui/src/main/kotlin/n7/ad2/hero/page/internal/pager/HeroPageToolbar.kt`
- Rewrite: `feature/hero-page/ui/src/main/kotlin/n7/ad2/hero/page/internal/pager/HeroPageFragment.kt`
- Delete: `AnimatedToolbar.kt`
- Delete: `fragment_hero_page.xml`
- Modify: `feature/hero-page/ui/build.gradle.kts` (remove `libs.ticker`)

- [ ] **Step 1: Create `HeroPageToolbar.kt`**

```kotlin
package n7.ad2.hero.page.internal.pager

import android.view.OrientationEventListener
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import n7.ad2.AppLocale
import n7.ad2.core.ui.compose.AppTheme

@Composable
internal fun HeroPageToolbar(
    heroName: String,
    locale: AppLocale,
    currentPage: Int,
    onLocaleChange: (AppLocale) -> Unit,
) {
    var rotation by remember { mutableFloatStateOf(0f) }
    val context = LocalContext.current
    DisposableEffect(Unit) {
        val listener = object : OrientationEventListener(context) {
            override fun onOrientationChanged(orientation: Int) {
                if (orientation != ORIENTATION_UNKNOWN) rotation = 360f - orientation
            }
        }
        listener.enable()
        onDispose { listener.disable() }
    }
    TopAppBar(
        title = { Text(text = heroName, style = AppTheme.style.H6, color = AppTheme.color.textColor) },
        backgroundColor = AppTheme.color.surface,
        navigationIcon = {
            AnimatedVisibility(visible = currentPage == 0) {
                AsyncImage(
                    model = "file:///android_asset/heroes/$heroName/minimap.png",
                    contentDescription = null,
                    modifier = Modifier.padding(start = 12.dp).size(30.dp).rotate(rotation),
                )
            }
        },
        actions = {
            AnimatedVisibility(visible = currentPage == 1) {
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

- [ ] **Step 2: Create `HeroPageScreen.kt`**

```kotlin
package n7.ad2.hero.page.internal.pager

import android.os.Bundle
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.core.os.bundleOf
import androidx.fragment.compose.AndroidFragment
import kotlinx.coroutines.launch
import n7.ad2.AppLocale
import n7.ad2.feature.hero.page.ui.R
import n7.ad2.hero.page.internal.guides.HeroGuideFragment
import n7.ad2.hero.page.internal.info.HeroInfoFragment
import n7.ad2.hero.page.internal.responses.ResponsesFragment

@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun HeroPageScreen(
    heroName: String,
    appLocale: AppLocale,
    onLocaleChange: (AppLocale) -> Unit,
) {
    val pagerState = rememberPagerState(pageCount = { 3 })
    val scope = rememberCoroutineScope()
    val tabTitles = listOf(R.string.hero_info, R.string.hero_sound, R.string.hero_guide)
    val args: Bundle = bundleOf("HERO_NAME" to heroName)

    Column(Modifier.fillMaxSize().statusBarsPadding()) {
        HeroPageToolbar(
            heroName = heroName,
            locale = appLocale,
            currentPage = pagerState.currentPage,
            onLocaleChange = onLocaleChange,
        )
        TabRow(selectedTabIndex = pagerState.currentPage) {
            tabTitles.forEachIndexed { idx, titleRes ->
                Tab(
                    selected = pagerState.currentPage == idx,
                    onClick = { scope.launch { pagerState.animateScrollToPage(idx) } },
                    text = { Text(stringResource(titleRes)) },
                )
            }
        }
        HorizontalPager(state = pagerState, modifier = Modifier.fillMaxSize()) { page ->
            when (page) {
                0 -> AndroidFragment<HeroInfoFragment>(arguments = args)
                1 -> AndroidFragment<ResponsesFragment>(arguments = args)
                2 -> AndroidFragment<HeroGuideFragment>(arguments = args)
            }
        }
    }
}
```

**Note:** The key `"HERO_NAME"` string must match the `HERO_NAME` constant in each child Fragment's `companion object`. Confirmed: all 3 child Fragments use `private const val HERO_NAME = "HERO_NAME"` — the literal `"HERO_NAME"` is safe, though extracting to a shared `const val` would be cleaner (optional).

- [ ] **Step 3: Rewrite `HeroPageFragment.kt`**

```kotlin
package n7.ad2.hero.page.internal.pager

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import n7.ad2.AppInformation
import n7.ad2.android.findDependencies
import n7.ad2.core.ui.content
import n7.ad2.hero.page.internal.di.DaggerHeroPageComponent
import n7.ad2.ktx.lazyUnsafe
import javax.inject.Inject

class HeroPageFragment : Fragment() {

    companion object {
        private const val HERO_NAME = "HERO_NAME"
        fun getInstance(heroName: String): HeroPageFragment = HeroPageFragment().apply {
            arguments = bundleOf(HERO_NAME to heroName)
        }
    }

    @Inject lateinit var appInformation: AppInformation
    private val heroName by lazyUnsafe { requireArguments().getString(HERO_NAME)!! }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        DaggerHeroPageComponent.factory().create(findDependencies()).inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
        content {
            HeroPageScreen(
                heroName = heroName,
                appLocale = appInformation.appLocale,
                onLocaleChange = { /* propagated via child ResponsesFragment via setFragmentResult if needed later */ },
            )
        }
}
```

**Note on locale propagation:** The original `AnimatedToolbar` had `setOnChangeHeroLocaleListener` that forwarded the user's locale click into `ResponsesFragment.viewModel.loadResponses(locale)`. After migration, the locale click happens in `HeroPageToolbar` inside the parent Fragment, but `ResponsesViewModel` lives in the child `ResponsesFragment`. Wire via `Fragment.setFragmentResult(REQUEST, bundleOf("locale" to locale.value))` in the `onLocaleChange` lambda, and add a `setFragmentResultListener` in `ResponsesFragment.onCreateView` that calls `viewModel.loadResponses(locale)`. If this is more work than desired for an initial commit, leave the lambda empty — the default locale from `appInformation` already loads responses correctly; only manual user-triggered locale switch from toolbar breaks.

- [ ] **Step 4: Delete `AnimatedToolbar.kt`**

```bash
rm feature/hero-page/ui/src/main/kotlin/n7/ad2/hero/page/internal/pager/AnimatedToolbar.kt
```

If `feature/hero-page/ui/src/main/kotlin/n7/ad2/hero/page/internal/pager/ViewPager2Adapter.kt` exists (it's referenced but not shown in earlier exploration — check `pager/` directory), also delete it:

```bash
ls feature/hero-page/ui/src/main/kotlin/n7/ad2/hero/page/internal/pager/
```

If `ViewPager2Adapter.kt` is listed: `rm feature/hero-page/ui/src/main/kotlin/n7/ad2/hero/page/internal/pager/ViewPager2Adapter.kt`.

- [ ] **Step 5: Delete `fragment_hero_page.xml`**

```bash
rm feature/hero-page/ui/src/main/res/layout/fragment_hero_page.xml
```

- [ ] **Step 6: Remove `libs.ticker` dependency**

Edit `feature/hero-page/ui/build.gradle.kts`. Remove the line:

```kotlin
    implementation(libs.ticker)
```

- [ ] **Step 7: Build verification**

```
./gradlew :feature:hero-page:ui:assembleDebug 2>&1 | tail -20
```

Expected: **BUILD SUCCESSFUL**.

- [ ] **Step 8: Commit**

```bash
git add feature/hero-page/ui/
git commit -m "master: feat(hero-page/ui): migrate Hero Page pager shell to Compose"
```

---

## Task 7: Final verification and cleanup

- [ ] **Step 1: Verify `res/layout/` is empty**

```bash
ls feature/hero-page/ui/src/main/res/layout/ 2>/dev/null
```

Expected: empty output (no files). If the directory still has files, determine which screen they belong to and delete them (add to the appropriate task's step in follow-up).

- [ ] **Step 2: Remove empty layout directory**

```bash
rmdir feature/hero-page/ui/src/main/res/layout/
```

If `rmdir` refuses because of leftover files, list them and delete as appropriate.

- [ ] **Step 3: Verify no lingering `databinding` or ViewBinding imports**

```bash
grep -rn "feature.hero.page.ui.databinding\|FragmentHeroInfoBinding\|FragmentHeroPageBinding\|FragmentHeroGuideBinding\|FragmentHeroResponsesBinding\|HeroStatisticsBinding\|DialogResponseBinding" feature/hero-page/ui/src/main/kotlin/
```

Expected: zero matches. If matches remain, delete or update those lines.

- [ ] **Step 4: Run the demo build — the primary success gate**

```
./gradlew :feature:hero-page:demo:assembleDebug 2>&1 | tail -20
```

Expected: **BUILD SUCCESSFUL**. This is the task described in the user's original request.

- [ ] **Step 5: Clean `build/` directory to confirm no stale generated classes cause false success**

```
./gradlew :feature:hero-page:ui:clean :feature:hero-page:demo:clean && ./gradlew :feature:hero-page:demo:assembleDebug 2>&1 | tail -10
```

Expected: BUILD SUCCESSFUL from a clean build.

- [ ] **Step 6: Commit cleanup (if anything)**

```bash
git status
```

If `res/layout/` removal wasn't captured in earlier commits (directory-only removals sometimes aren't), commit:

```bash
git add feature/hero-page/ui/
git commit -m "master: chore(hero-page/ui): remove empty layout directory"
```

If nothing to commit, the migration is complete.

---

## Summary

After all tasks complete, `feature/hero-page/ui`:

- Has **zero** XML layout files.
- Has **zero** `RecyclerView.Adapter`, `ViewHolder`, or `ItemDecoration` code.
- Has **zero** custom Kotlin `View` classes (`AnimatedToolbar`, `HeroFlow`, `HeroStatistics` all gone).
- Has **zero** `DialogFragment` (`DialogResponse` replaced by Compose `AlertDialog`).
- Uses `HorizontalPager` + `TabRow` + `AndroidFragment` for the 3-tab pager.
- Uses `LazyColumn.stickyHeader` for sticky section headers.
- Uses `FlowRow` for the guide chip rows.
- Uses `AsyncImage` (coil-compose) for all image loads.
- Uses `ErrorScreen` and `LoadingAnimation` from `core/ui` for standard states.
- Two fragments (`HeroInfoFragment`, `HeroGuideFragment`) are now **actually rendering** content (they weren't before — adapters were stubbed with `TODO()`).
- `:feature:hero-page:demo:assembleDebug` compiles and packages successfully.

**Follow-up (explicit non-goals of this plan):**

- Compose UI tests / screenshot tests.
- Proper locale-change propagation between `HeroPageToolbar` and `ResponsesFragment` (currently lambda is a no-op; see Task 6 Step 3 note).
- Inline download-progress rendering in `ResponseBodyItem` (currently dropped; was in old `DownloadResult.InProgress` handling).
- Migration of `feature/items`, `feature/streams`, etc. — those stay on XML.
- Removing `libs.ticker` from the project-level catalog (other modules may still use it; only removed from `hero-page/ui`).
