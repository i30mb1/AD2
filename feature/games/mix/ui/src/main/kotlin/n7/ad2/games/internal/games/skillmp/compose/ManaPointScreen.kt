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
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import n7.ad2.core.ui.compose.AppTheme
import n7.ad2.core.ui.compose.view.ErrorScreen
import n7.ad2.core.ui.compose.view.LoadingScreen
import n7.ad2.feature.games.mix.ui.R
import n7.ad2.games.internal.games.skillmp.SkillGameViewModel
import n7.ad2.games.internal.games.skillmp.SpellMatch
import n7.ad2.games.room.GameMode

private val MyColor = Color(0xFFF44336)
private val PeerColor = Color(0xFF448AFF)

@Composable
internal fun ManaPointScreen(state: SkillGameViewModel.State, onVariantClick: (spell: SkillGameViewModel.Spell) -> Unit) {
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
            state.isEndGame -> EndGameScreen(state.match, state.peerName, state.newRecord)
            state.isError -> ErrorScreen()
            else -> {
                MatchHeader(state.match, state.peerName)
                val compare = state.compare
                Box(Modifier.weight(1f), contentAlignment = Alignment.Center) {
                    when {
                        state.isLoading -> LoadingScreen()
                        compare != null -> CompareCards(compare, state.spellList, state.showRightAnswer, onVariantClick)
                        else -> GuessSpellImage(state.spellImage, state.spellLVL)
                    }
                }
                if (!state.isLoading && compare == null) {
                    VariantBlocks(state.spellList, state.showRightAnswer, state.selectedSpell, onVariantClick)
                }
            }
        }
    }
}

@Composable
private fun MatchHeader(match: SpellMatch, peerName: String?) {
    when (match.mode) {
        GameMode.SOLO -> Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Counter(match.myScore)
            Lives(match.lives)
        }
        GameMode.VERSUS -> Column(Modifier.fillMaxWidth()) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Score(match.myScore, MyColor)
                Text(
                    text = stringResource(R.string.game_round, match.round + 1, SpellMatch.VERSUS_ROUNDS),
                    color = AppTheme.color.textSecondaryColor,
                    style = AppTheme.style.body,
                )
                Score(match.peerScore, PeerColor)
            }
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(stringResource(R.string.game_you), color = AppTheme.color.textSecondaryColor, style = AppTheme.style.info)
                val answered = if (match.peerAnswer != null) " · " + stringResource(R.string.game_answered) else ""
                Text(peerName.orEmpty() + answered, color = AppTheme.color.textSecondaryColor, style = AppTheme.style.info)
            }
        }
        GameMode.COOP -> Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Counter(match.myScore)
            Lives(match.lives)
            if (match.peerAnswer != null) {
                Text(peerName.orEmpty() + " · " + stringResource(R.string.game_answered), color = AppTheme.color.textSecondaryColor, style = AppTheme.style.info)
            }
        }
    }
}

@Composable
private fun Score(score: Int, color: Color) {
    Text(text = score.toString(), color = color, fontSize = 28.sp, fontWeight = FontWeight.Bold)
}

@Composable
private fun Lives(lives: Int) {
    Text(
        text = "❤️".repeat(lives.coerceAtLeast(0)) + "🖤".repeat((3 - lives).coerceAtLeast(0)),
        fontSize = 18.sp,
        modifier = Modifier.padding(top = 4.dp),
    )
}

@Composable
private fun GuessSpellImage(spellImage: String, spellLVL: Int) {
    val infiniteTransition = rememberInfiniteTransition()
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(tween(1000), RepeatMode.Reverse),
    )
    Column(
        modifier = Modifier.scale(scale),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        GuessSpellImage(spellImage) { }
        Row(modifier = Modifier.padding(top = 4.dp)) {
            repeat(spellLVL) {
                Box(
                    modifier = Modifier
                        .padding(2.dp, 0.dp)
                        .size(20.dp, 10.dp)
                        .background(Color.Black)
                        .padding(0.5.dp)
                        .background(Color.Yellow),
                )
            }
        }
    }
}
