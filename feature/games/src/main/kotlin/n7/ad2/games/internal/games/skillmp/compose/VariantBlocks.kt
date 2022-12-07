package n7.ad2.games.internal.games.skillmp.compose

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.movableContentOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import n7.ad2.games.internal.games.skillmp.SkillGameViewModel
import n7.ad2.ui.compose.AppTheme

@Composable
internal fun VariantBlocks(
    counter: Int,
    spellList: SkillGameViewModel.SpellList,
    showRightAnswer: Boolean,
    selectedSpell: SkillGameViewModel.Spell?,
    onVariantClick: (spell: SkillGameViewModel.Spell) -> Unit,
    modifier: Modifier = Modifier,
) {
    val movableBlock =
        movableContentOf {
            for (spell in spellList.list) {
                Block(spell, showRightAnswer, spell == selectedSpell, onVariantClick)
            }
        }

    when (counter % 3) {
        0 -> Row(modifier) { movableBlock() }
        1 -> Column(modifier) { movableBlock() }
        2 -> Box(modifier) { movableBlock() }
    }
}

@Preview
@Composable
private fun BlockPreview() {
    Block(
        spell = SkillGameViewModel.Spell("10", false),
        showRightAnswer = false,
        isSelected = false,
        onVariantClick = {}
    )
}

@Composable
private fun Block(
    spell: SkillGameViewModel.Spell,
    showRightAnswer: Boolean,
    isSelected: Boolean,
    onVariantClick: (spell: SkillGameViewModel.Spell) -> Unit,
    modifier: Modifier = Modifier,
) {
    val isSmall by remember { mutableStateOf(isSelected) }
//    isSmall = showRightAnswer && spell.isRightAnswer
    val backgroundColor = if (showRightAnswer && spell.isRightAnswer) AppTheme.color.primary else AppTheme.color.surface
    val scale by animateFloatAsState(targetValue = if (isSmall) 0.7f else 1f)

    Button(
        modifier = modifier
            .sizeIn(45.dp, 45.dp)
            .padding(4.dp, 2.dp)
            .scale(scale)
            .clip(AppTheme.shape.small),
        colors = ButtonDefaults.buttonColors(backgroundColor = backgroundColor),
        onClick = { onVariantClick(spell) },
    ) {
        Box {
            Text(
                text = spell.cost,
                maxLines = 1,
                fontWeight = FontWeight.Bold,
                color = AppTheme.color.textColor,
                modifier = Modifier
                    .align(Alignment.Center),
            )
        }
    }
}