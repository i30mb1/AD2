package n7.ad2.games.internal.games.skillmp.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import n7.ad2.core.ui.compose.AppTheme
import n7.ad2.games.internal.compose.AnswerLook
import n7.ad2.games.internal.compose.MeColor
import n7.ad2.games.internal.compose.RightColor
import n7.ad2.games.internal.compose.answerLook
import n7.ad2.games.internal.games.skillmp.SkillGameViewModel

private val answerShape = RoundedCornerShape(12.dp)

/** Answers as a 2x2 grid: numbers big, hero names a bit smaller so they fit. */
@Composable
internal fun VariantBlocks(
    spellList: SkillGameViewModel.SpellList,
    showRightAnswer: Boolean,
    selectedSpell: SkillGameViewModel.Spell?,
    onVariantClick: (spell: SkillGameViewModel.Spell) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        for (row in spellList.list.chunked(2)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                for (spell in row) {
                    val look = answerLook(isRight = spell.isRightAnswer, isPicked = spell == selectedSpell, reveal = showRightAnswer)
                    Block(spell.cost, look, enabled = !showRightAnswer, Modifier.weight(1f)) { onVariantClick(spell) }
                }
            }
        }
    }
}

@Composable
private fun Block(text: String, look: AnswerLook, enabled: Boolean, modifier: Modifier, onClick: () -> Unit) {
    val (background, border) = when (look) {
        AnswerLook.IDLE -> AppTheme.color.surface to Color.Transparent
        AnswerLook.PICKED -> AppTheme.color.surface to AppTheme.color.textSecondaryColor
        AnswerLook.RIGHT -> RightColor.copy(alpha = 0.25f) to RightColor
        AnswerLook.WRONG -> MeColor.copy(alpha = 0.2f) to MeColor
    }
    Box(
        modifier = modifier
            .height(56.dp)
            .clip(answerShape)
            .background(background)
            .border(1.dp, border, answerShape)
            .clickable(enabled = enabled, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            color = AppTheme.color.textColor,
            fontSize = if (text.all(Char::isDigit)) 20.sp else 15.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Preview(backgroundColor = 0xFF202225, showBackground = true, widthDp = 360)
@Composable
private fun VariantBlocksPreview() {
    val right = SkillGameViewModel.Spell("350", true)
    val wrong = SkillGameViewModel.Spell("500", false)
    AppTheme(isDarkTheme = true) {
        VariantBlocks(
            spellList = SkillGameViewModel.SpellList(listOf(SkillGameViewModel.Spell("200", false), right, wrong, SkillGameViewModel.Spell("700", false))),
            showRightAnswer = true,
            selectedSpell = wrong,
            onVariantClick = {},
        )
    }
}
