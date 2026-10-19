package n7.ad2.hero.page.internal.info.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import n7.ad2.core.ui.compose.AppTheme
import n7.ad2.feature.hero.page.ui.R
import n7.ad2.heroes.domain.model.Facet
import n7.ad2.heroes.domain.model.TalentLevel

/** Same layout as the in-game tree: level 25 on top, left and right options around the level. */
@Composable
internal fun TalentsCard(talents: List<TalentLevel>) {
    val top = talents.maxByOrNull { it.level }
    val summary = top?.let { "${it.level}: ${it.left} / ${it.right}" }
    CollapsibleCard(title = stringResource(R.string.info_talents), summary = summary) {
        talents.sortedByDescending { it.level }.forEachIndexed { index, talent ->
            if (index > 0) VSpace(8.dp)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(IntrinsicSize.Min),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                TalentOption(talent.left, TextAlign.End, Modifier.weight(1f))
                Box(
                    modifier = Modifier
                        .padding(horizontal = 8.dp)
                        .size(36.dp)
                        .clip(RoundedCornerShape(50))
                        .background(AppTheme.color.background)
                        .border(1.5.dp, InfoColors.pure, RoundedCornerShape(50)),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(talent.level.toString(), fontSize = 14.sp, fontWeight = FontWeight.Bold, color = InfoColors.pure)
                }
                TalentOption(talent.right, TextAlign.Start, Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun TalentOption(text: String, align: TextAlign, modifier: Modifier) {
    Box(
        modifier = modifier
            .fillMaxHeight()
            .clip(AppTheme.shape.medium)
            .background(InfoColors.chip)
            .padding(horizontal = 10.dp, vertical = 8.dp),
        contentAlignment = if (align == TextAlign.End) Alignment.CenterEnd else Alignment.CenterStart,
    ) {
        Text(text, style = AppTheme.style.body.copy(lineHeight = 16.sp), color = AppTheme.color.textColor, textAlign = align)
    }
}

@Composable
internal fun FacetsCard(facets: List<Facet>) {
    CollapsibleCard(title = stringResource(R.string.info_facets), summary = facets.joinToString(" · ") { it.name }) {
        facets.forEachIndexed { index, facet ->
            if (index > 0) InfoDivider(Modifier.padding(vertical = 10.dp))
            Text(facet.name, style = AppTheme.style.H5, fontWeight = FontWeight.Bold, color = AppTheme.color.textColor)
            if (facet.description.isNotEmpty()) {
                VSpace(4.dp)
                RichText(facet.description, color = AppTheme.color.textSecondaryColor)
            }
        }
    }
}

@Composable
internal fun BioCard(bio: String) {
    CollapsibleCard(title = stringResource(R.string.hero_fragment_bio), summary = bio.substringBefore('\n').substringBefore(". ")) {
        RichText(text = bio, color = AppTheme.color.textSecondaryColor)
    }
}

@Composable
internal fun TriviaCard(trivia: List<String>) {
    CollapsibleCard(title = stringResource(R.string.hero_fragment_trivia), summary = trivia.first()) {
        trivia.forEach { fact -> BulletText(fact, color = AppTheme.color.textColor) }
    }
}
