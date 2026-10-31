package n7.ad2.games.internal.games.skillmp.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import n7.ad2.core.ui.compose.AppTheme
import n7.ad2.core.ui.compose.view.ErrorScreen
import n7.ad2.core.ui.compose.view.LoadingScreen
import n7.ad2.feature.games.mix.ui.R
import n7.ad2.games.internal.compose.Avatar
import n7.ad2.games.internal.compose.DimColor
import n7.ad2.games.internal.compose.MeColor
import n7.ad2.games.internal.compose.DuoHud
import n7.ad2.games.internal.compose.GoldColor
import n7.ad2.games.internal.compose.HudBar
import n7.ad2.games.internal.compose.LineColor
import n7.ad2.games.internal.compose.PeerColor
import n7.ad2.games.internal.compose.SoloHud
import n7.ad2.games.internal.games.skillmp.Answer
import n7.ad2.games.internal.games.skillmp.SkillGameViewModel
import n7.ad2.games.internal.games.skillmp.SpellMatch
import n7.ad2.games.room.GameMode

private const val MAX_LIVES = 3
private const val SPELL_LEVELS = 3

@Composable
internal fun ManaPointScreen(
    state: SkillGameViewModel.State,
    onVariantClick: (spell: SkillGameViewModel.Spell) -> Unit,
    onToggleTile: (Int) -> Unit = {},
    onBuild: () -> Unit = {},
    onAgain: () -> Unit = {},
    onOtherGame: () -> Unit = {},
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppTheme.color.background)
            .systemBarsPadding()
            .padding(horizontal = 16.dp, vertical = 12.dp),
    ) {
        when {
            state.isEndGame -> EndGameScreen(state, onAgain, onOtherGame)
            state.isError -> ErrorScreen()
            else -> {
                MatchHeader(state.match, state.peerName, state.streak)
                Spacer(Modifier.height(16.dp))
                val compare = state.compare
                val build = state.build
                when {
                    state.countdown > 0 -> Box(Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) { StartCountdown(state.countdown, state.peerName) }
                    state.isLoading -> Box(Modifier.weight(1f), contentAlignment = Alignment.Center) { LoadingScreen() }
                    build != null -> BuildBoard(build, state.selectedTiles, state.showRightAnswer, onToggleTile, onBuild)
                    compare != null -> CompareCards(compare, state.spellList, state.showRightAnswer, onVariantClick, Modifier.weight(1f))
                    else -> {
                        // имена героев вместо чисел - значит это «Угадай героя»
                        val isHero = state.spellList.list.any { it.cost.toIntOrNull() == null }
                        Box(Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.TopCenter) {
                            QuestionCard(state.spellImage, if (isHero) 0 else state.spellLVL, isHero)
                        }
                        VariantBlocks(state.spellList, state.showRightAnswer, state.selectedSpell, onVariantClick)
                    }
                }
            }
        }
    }
}

@Composable
private fun MatchHeader(match: SpellMatch, peerName: String?, streak: Int) {
    val points = pluralStringResource(R.plurals.game_points, match.myScore, match.myScore)
    when (match.mode) {
        GameMode.SOLO -> SoloHud(score = points, extra = streakText(streak), lives = match.lives, maxLives = MAX_LIVES)
        GameMode.VERSUS -> Column {
            DuoHud(
                me = stringResource(R.string.game_me_letter),
                myScore = match.myScore.toString(),
                peerName = peerName.orEmpty(),
                peerScore = match.peerScore.toString(),
                centerLabel = stringResource(R.string.game_round_label),
                centerValue = "${match.round + 1} / ${SpellMatch.VERSUS_ROUNDS}",
                peerAnswered = match.peerAnswer != null,
            )
            Spacer(Modifier.height(12.dp))
            HudBar(
                progress = (match.round + 1f) / SpellMatch.VERSUS_ROUNDS,
                start = "",
                end = if (match.peerAnswer != null) "${peerName.orEmpty()} · ${stringResource(R.string.game_answered)}" else "",
                endColor = PeerColor,
            )
        }
        GameMode.COOP -> Column {
            SoloHud(score = stringResource(R.string.game_shared_points, match.myScore), extra = streakText(streak), lives = match.lives, maxLives = MAX_LIVES)
            if (match.peerAnswer != null) {
                Text(
                    text = "${peerName.orEmpty()} · ${stringResource(R.string.game_answered)}",
                    color = PeerColor,
                    fontSize = 11.sp,
                    modifier = Modifier.padding(top = 8.dp),
                )
            }
        }
    }
}

/** Серия видна с двух верных подряд, одиночный верный ответ - ещё не серия */
@Composable
private fun streakText(streak: Int): String? = if (streak >= 2) stringResource(R.string.game_streak, streak) else null

/** Общий отсчёт перед первым вопросом: вдвоём аватарки по краям, число посередине */
@Composable
private fun StartCountdown(seconds: Int, peerName: String?) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text(stringResource(R.string.game_starts_in), color = DimColor, fontSize = 13.sp)
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(28.dp)) {
            if (peerName != null) Avatar(stringResource(R.string.game_me_letter), MeColor, 56.dp)
            Box(Modifier.size(88.dp).clip(CircleShape).border(4.dp, MeColor, CircleShape), contentAlignment = Alignment.Center) {
                Text("$seconds", color = AppTheme.color.textColor, fontSize = 40.sp, fontWeight = FontWeight.Bold)
            }
            if (peerName != null) Avatar(peerName, PeerColor, 56.dp)
        }
    }
}

/** The question in a card: spell icon with its level, or the hero picture. */
@Composable
private fun QuestionCard(image: String, level: Int, isHero: Boolean) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(AppTheme.color.surface)
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(stringResource(if (isHero) R.string.game_question_hero else R.string.game_question_mana), color = DimColor, fontSize = 13.sp)
        GuessSpellImage(image) { }
        if (level > 0) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(stringResource(R.string.game_spell_level), color = DimColor, fontSize = 12.sp)
                repeat(SPELL_LEVELS) { index ->
                    Box(Modifier.size(16.dp, 4.dp).clip(RoundedCornerShape(2.dp)).background(if (index < level) GoldColor else LineColor))
                }
            }
        }
    }
}

@Preview(backgroundColor = 0xFF202225, showBackground = true, widthDp = 390, heightDp = 844)
@Composable
private fun ManaPointVersusPreview() {
    val wrong = SkillGameViewModel.Spell("500", false)
    val match = SpellMatch.start(GameMode.VERSUS).copy(round = 4, myScore = 3, peerScore = 2, peerAnswer = Answer(true, 900))
    val state = SkillGameViewModel.State.init(match, peerName = "Pixel 8").copy(
        isLoading = false,
        spellList = SkillGameViewModel.SpellList(listOf(SkillGameViewModel.Spell("200", false), SkillGameViewModel.Spell("350", true), wrong, SkillGameViewModel.Spell("700", false))),
        spellLVL = 2,
        showRightAnswer = true,
        selectedSpell = wrong,
    )
    AppTheme(isDarkTheme = true) { ManaPointScreen(state, onVariantClick = {}) }
}
