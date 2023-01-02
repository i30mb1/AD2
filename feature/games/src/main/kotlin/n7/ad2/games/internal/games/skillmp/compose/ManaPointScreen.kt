package n7.ad2.games.internal.games.skillmp.compose

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import n7.ad2.games.internal.games.skillmp.SkillGameViewModel
import n7.ad2.ui.compose.AppTheme
import n7.ad2.ui.compose.view.ErrorScreen
import n7.ad2.ui.compose.view.LoadingScreen

@Composable
internal fun ManaPointScreen(
    state: SkillGameViewModel.State,
    onVariantClick: (spell: SkillGameViewModel.Spell) -> Unit,
) {
    val color by rememberUpdatedState(newValue = Color(state.backgroundColor).copy(alpha = 0.2f))
    val animateColor = animateColorAsState(targetValue = color, animationSpec = tween(2_000))
    Column(
        modifier = Modifier
            .fillMaxSize()
            .drawBehind { drawRect(animateColor.value) }
            .systemBarsPadding()
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        when {
            state.isLoading -> {
                Box {
                    LoadingScreen()
                    if (state.loadingAttempts > 0) {
                        Text(
                            text = "${state.loadingAttempts}",
                            color = AppTheme.color.textColor,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                }
            }
            state.isError -> ErrorScreen()
            state.isEndGame -> EndGameScreen(state.userScore)
            else -> {
                Counter(state.userScore)
                GuessSpellImage(
                    Modifier
                        .weight(1f),
                    state.spellImage,
                    state.spellLVL,
                ) { }
                VariantBlocks(
                    state.spellList,
                    state.showRightAnswer,
                    state.selectedSpell,
                    onVariantClick,
                )
            }

        }
    }
}

@Composable
private fun GuessSpellImage(
    modifier: Modifier = Modifier,
    spellImage: String,
    spellLVL: Int,
    onSpellClick: () -> Unit,
) {
    val infiniteTransition = rememberInfiniteTransition()
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(tween(1000), RepeatMode.Reverse)
    )
    Column(
        modifier = modifier
            .fillMaxSize()
            .scale(scale),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        GuessSpellImage(spellImage, onSpellClick)
        SpellLVL(spellLVL)
    }
}

@Composable
private fun SpellLVL(spellLVL: Int) {
    Row(
        modifier = Modifier.padding(top = 4.dp)
    ) {
        repeat(spellLVL) {
            Box(
                modifier = Modifier
                    .padding(2.dp, 0.dp)
                    .size(20.dp, 10.dp)
                    .background(Color.Black)
                    .padding(0.5.dp)
                    .background(Color.Yellow)
            ) {

            }
        }
    }
}

@Preview
@Composable
private fun ManaPointScreenPreviewLoading(
    @PreviewParameter(PreviewStateProvider::class) state: SkillGameViewModel.State,
) {
    ManaPointScreen(state) {}
}

private class PreviewStateProvider : PreviewParameterProvider<SkillGameViewModel.State> {
    override val values: Sequence<SkillGameViewModel.State> = sequenceOf(
        SkillGameViewModel.State.init(),
        SkillGameViewModel.State(
            false,
            0,
            false,
            android.R.color.background_dark,
            "",
            SkillGameViewModel.SpellList(
                listOf(
                    SkillGameViewModel.Spell("10", false),
                    SkillGameViewModel.Spell("20", false),
                    SkillGameViewModel.Spell("30", true),
                    SkillGameViewModel.Spell("40", false),
                )
            ),
            3,
            false,
            null,
            0,
            0,
            false,
        ),
        SkillGameViewModel.State.init().copy(isEndGame = true, userScore = 5),
    )
}