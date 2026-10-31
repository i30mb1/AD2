package n7.ad2.games.internal.games.skillmp.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import n7.ad2.core.ui.compose.AppTheme
import n7.ad2.feature.games.mix.ui.R
import n7.ad2.games.internal.games.skillmp.GetSkillsUseCase
import n7.ad2.games.internal.games.skillmp.SkillGameViewModel

private val Right = Color(0xFF43A047)
private val Wrong = Color(0xFFF44336)

/** «Больше или меньше»: вопрос и две карточки героев, значения открываются после ответа */
@Composable
internal fun CompareCards(
    compare: GetSkillsUseCase.Compare,
    options: SkillGameViewModel.SpellList,
    showAnswer: Boolean,
    onPick: (SkillGameViewModel.Spell) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        Text(stringResource(compare.question), color = AppTheme.color.textColor, fontSize = 20.sp, fontWeight = FontWeight.SemiBold, textAlign = TextAlign.Center)
        Text(stringResource(R.string.compare_level_hint), color = AppTheme.color.textSecondaryColor, style = AppTheme.style.info, modifier = Modifier.padding(bottom = 16.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            listOf(compare.left, compare.right).forEachIndexed { index, side ->
                val option = options.list.getOrNull(index) ?: return@forEachIndexed
                SideCard(side, option, showAnswer, Modifier.weight(1f)) { onPick(option) }
            }
        }
    }
}

@Composable
private fun SideCard(side: GetSkillsUseCase.Side, option: SkillGameViewModel.Spell, showAnswer: Boolean, modifier: Modifier, onClick: () -> Unit) {
    val outline = when {
        !showAnswer -> Modifier
        option.isRightAnswer -> Modifier.border(2.dp, Right, RoundedCornerShape(14.dp))
        else -> Modifier.border(2.dp, Wrong, RoundedCornerShape(14.dp))
    }
    Column(
        modifier = modifier
            .alpha(if (showAnswer && !option.isRightAnswer) 0.7f else 1f)
            .then(outline)
            .clip(RoundedCornerShape(14.dp))
            .background(AppTheme.color.surface)
            .clickable(enabled = !showAnswer, onClick = onClick)
            .padding(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        AsyncImage(
            model = side.imageUrl,
            contentDescription = side.name,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(0.8f)
                .clip(RoundedCornerShape(10.dp)),
        )
        Text(side.name, color = AppTheme.color.textColor, fontWeight = FontWeight.SemiBold, maxLines = 1, modifier = Modifier.padding(top = 8.dp))
        Text(
            text = if (showAnswer) side.value + if (option.isRightAnswer) " ✓" else "" else "?",
            color = if (showAnswer && option.isRightAnswer) Right else AppTheme.color.textColor,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 4.dp),
        )
    }
}
