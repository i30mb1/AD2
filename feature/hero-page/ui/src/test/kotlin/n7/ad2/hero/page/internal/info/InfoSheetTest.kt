package n7.ad2.hero.page.internal.info

import androidx.activity.ComponentActivity
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.hasAnyDescendant
import androidx.compose.ui.test.hasScrollAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performScrollToNode
import n7.ad2.core.ui.compose.AppTheme
import n7.ad2.hero.page.internal.info.domain.vo.VOHeroInfo
import n7.ad2.heroes.domain.model.HeroDescription
import n7.ad2.heroes.domain.model.PatchChange
import n7.ad2.heroes.domain.model.PatchNote
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/** Rarely needed parts of the Info page open in one sheet, a row after another. */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
internal class InfoSheetTest {

    @get:Rule val compose = createAndroidComposeRule<ComponentActivity>()

    private val info = VOHeroInfo(
        heroImageUrl = "",
        description = HeroDescription(
            name = "Abaddon",
            bio = "Abaddon was born to the House Avernus.",
            trivia = List(40) { index -> "Fact number $index about Abaddon." },
            changes = listOf(PatchChange("7.41d", 0, List(40) { index -> PatchNote("Change number $index", 0) }, emptyList(), emptyList())),
        ),
        abilityGroups = emptyList(),
    )

    private fun sheetWith(text: String) = compose.onNode(hasScrollAction() and hasAnyDescendant(hasText(text)))

    private fun scrollOf(text: String): Float = sheetWith(text).fetchSemanticsNode().config[SemanticsProperties.VerticalScrollAxisRange].value()

    @Test
    fun `a row opens its sheet from the top, not where the previous sheet was scrolled to`() {
        compose.setContent { AppTheme(isDarkTheme = true) { HeroInfoContent(info, guide = null) } }

        compose.onNodeWithText("Lore").performScrollTo().performClick()
        sheetWith("Fact number 0 about Abaddon.").performScrollToNode(hasText("Fact number 39 about Abaddon."))
        assertTrue(scrollOf("Fact number 0 about Abaddon.") > 0f)
        compose.runOnUiThread { compose.activity.onBackPressedDispatcher.onBackPressed() }
        compose.waitForIdle()

        compose.onNodeWithText("Patch changes").performScrollTo().performClick()
        compose.waitForIdle()

        assertEquals(0f, scrollOf("Change number 0"))
    }
}
