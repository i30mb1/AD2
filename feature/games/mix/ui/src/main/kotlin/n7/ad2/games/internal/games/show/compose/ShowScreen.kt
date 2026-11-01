package n7.ad2.games.internal.games.show.compose

import android.os.SystemClock
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameMillis
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import n7.ad2.core.ui.compose.AppTheme
import n7.ad2.feature.games.mix.ui.R
import n7.ad2.games.internal.compose.BuzzOnAnswer
import n7.ad2.games.internal.compose.DimColor
import n7.ad2.games.internal.compose.LineColor
import n7.ad2.games.internal.compose.avatarLetter
import n7.ad2.games.internal.games.show.DailyPlan
import n7.ad2.games.internal.games.show.Fuse
import n7.ad2.games.internal.games.show.RoundAnswer
import n7.ad2.games.internal.games.show.ShowKind
import n7.ad2.games.internal.games.show.ShowMatch
import n7.ad2.games.internal.games.show.ShowOption
import n7.ad2.games.internal.games.show.ShowPlan
import n7.ad2.games.internal.games.show.ShowPlayer
import n7.ad2.games.internal.games.show.ShowQuestion
import n7.ad2.games.internal.games.show.ShowViewModel
import n7.ad2.games.internal.games.show.Voice
import n7.ad2.games.internal.games.show.points
import n7.ad2.games.internal.games.show.signed
import n7.ad2.games.internal.paper.Ink
import n7.ad2.games.internal.paper.InkDim
import n7.ad2.games.internal.paper.InkGood
import n7.ad2.games.internal.paper.PaperCard
import n7.ad2.games.internal.paper.PaperEdge
import n7.ad2.games.room.GameMode

@Immutable
internal class ShowActions(
    val onBet: () -> Unit = {},
    val onPick: (Int) -> Unit = {},
    val onTile: (Int) -> Unit = {},
    val onBuild: () -> Unit = {},
    val onBuzz: () -> Unit = {},
    val onFifty: () -> Unit = {},
    val onFreeze: () -> Unit = {},
    val onReplay: () -> Unit = {},
    val onAgain: () -> Unit = {},
    val onOtherGame: () -> Unit = {},
    val onShare: () -> Unit = {},
)

internal val ShowGold = Color(0xFFD9A23A)
private val Burn = Color(0xFFE8743B)
private val Ice = Color(0xFF8EC9F0)

/** The players' colors in the order of the room */
internal val PlayerColors = listOf(Color(0xFFD9483B), Color(0xFF3F7DD9), Color(0xFF3F9D57), Color(0xFF9B59B6), Color(0xFFE08E2B), Color(0xFF2BA6A6))

@Composable
internal fun ShowScreen(state: ShowViewModel.State, actions: ShowActions, clock: () -> Long = SystemClock::elapsedRealtime) {
    BuzzOnAnswer(state.myAnswer?.correct)
    // a frame clock for the fuse: it burns between the updates of the state
    var now by remember { mutableLongStateOf(clock()) }
    LaunchedEffect(Unit) {
        while (true) withFrameMillis { now = clock() }
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppTheme.color.background)
            .systemBarsPadding()
            .padding(horizontal = 16.dp, vertical = 12.dp),
    ) {
        if (state.phase == ShowViewModel.State.Phase.END) {
            if (state.daily) DailyEnd(state, actions) else ShowEnd(state, actions)
        } else {
            ShowPlay(state, now, actions)
        }
    }
}

@Composable
private fun ColumnScope.ShowPlay(state: ShowViewModel.State, now: Long, actions: ShowActions) {
    PlayersRow(state)
    Spacer(Modifier.height(16.dp))
    RoundHeader(state)
    Spacer(Modifier.height(12.dp))
    Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        val question = state.question
        when {
            state.countdown > 0 -> Countdown(state.countdown)
            state.loadFailed -> Text(stringResource(R.string.show_load_failed), color = AppTheme.color.textSecondaryColor, fontSize = 15.sp)
            question == null -> if (!state.daily) Intro(state, actions)
            else -> QuestionView(state, question, now, actions)
        }
    }
    if (state.question != null) {
        Spacer(Modifier.height(10.dp))
        FuseBar(state, now)
        if (!state.daily) {
            Spacer(Modifier.height(10.dp))
            PowerUps(state, actions)
        }
    }
}

/** Everyone in the show: the score so far and whether they answered this round */
@Composable
private fun PlayersRow(state: ShowViewModel.State) {
    val match = state.match
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        match.players.forEachIndexed { index, player ->
            val chipModifier = if (match.players.size > 1) Modifier.weight(1f) else Modifier.widthIn(min = 120.dp)
            PlayerChip(player, index, state, chipModifier)
        }
        if (match.players.size == 1) {
            Spacer(Modifier.weight(1f))
            Text(
                stringResource(
                    when {
                        state.daily -> R.string.daily
                        state.mode == GameMode.SOLO -> R.string.games_mode_solo
                        else -> R.string.games_mode_coop
                    },
                ),
                color = DimColor,
                fontSize = 12.sp,
                modifier = Modifier.align(Alignment.CenterVertically),
            )
        }
    }
}

@Composable
private fun PlayerChip(player: ShowPlayer, index: Int, state: ShowViewModel.State, modifier: Modifier) {
    val match = state.match
    val isMe = player.id == state.meId
    val asking = state.phase == ShowViewModel.State.Phase.QUESTION || state.phase == ShowViewModel.State.Phase.REVEAL
    val (status, statusColor) = when {
        player.id in match.gone -> stringResource(R.string.show_status_left) to InkDim
        !asking -> null to InkDim
        match.answerOf(player.id) != null -> stringResource(R.string.show_status_answered) to InkGood
        else -> stringResource(R.string.show_status_thinking) to InkDim
    }
    val shape = RoundedCornerShape(12.dp)
    Column(
        modifier = modifier
            .shadow(3.dp, shape)
            .clip(shape)
            .background(PaperCard)
            .border(3.dp, if (isMe) ShowGold else PaperEdge, shape)
            .padding(horizontal = 6.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        Box(Modifier.size(26.dp).clip(CircleShape).background(PlayerColors[index % PlayerColors.size]), contentAlignment = Alignment.Center) {
            Text(avatarLetter(player.name), color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }
        Text(if (isMe) stringResource(R.string.show_you) else player.name, color = InkDim, fontSize = 11.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
        Text(points(match.score(player.id)), color = Ink, fontSize = 16.sp, fontWeight = FontWeight.ExtraBold)
        Text(status.orEmpty(), color = statusColor, fontSize = 10.sp, maxLines = 1)
    }
}

/** The round number, the bet and what the round asks */
@Composable
private fun RoundHeader(state: ShowViewModel.State) {
    val round = state.match.current
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = when {
                round.final -> stringResource(R.string.show_final)
                state.daily -> stringResource(R.string.daily_question, round.index + 1, DailyPlan.QUESTIONS)
                else -> stringResource(R.string.show_round, round.index + 1, ShowPlan.ROUNDS)
            },
            color = if (round.final) ShowGold else DimColor,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp,
        )
        if (state.bet) {
            Text(
                stringResource(R.string.show_bet_chip),
                color = Ink,
                fontSize = 11.sp,
                fontWeight = FontWeight.ExtraBold,
                modifier = Modifier.clip(RoundedCornerShape(6.dp)).background(ShowGold).padding(horizontal = 6.dp, vertical = 2.dp),
            )
        }
    }
    Text(stringResource(round.kind.title()), color = AppTheme.color.textColor, fontSize = 26.sp, fontWeight = FontWeight.ExtraBold)
    val question = state.question
    val hint = when {
        round.final -> stringResource(R.string.show_hint_final)
        question is ShowQuestion.Pick && question.kind == ShowKind.MANA -> stringResource(R.string.show_hint_mana, question.spellLevel)
        else -> stringResource(round.kind.hint())
    }
    Text(hint, color = AppTheme.color.textSecondaryColor, fontSize = 13.sp, lineHeight = 17.sp)
}

/** Before the question: the kind of the round, the bet; the question loads meanwhile */
@Composable
private fun Intro(state: ShowViewModel.State, actions: ShowActions) {
    val round = state.match.current
    Spacer(Modifier.height(8.dp))
    if (round.final) {
        PaperPanel {
            Text(stringResource(R.string.show_final_rules), color = Ink, fontSize = 15.sp, lineHeight = 21.sp)
        }
        return
    }
    val enabled = !state.betUsed
    val shape = RoundedCornerShape(16.dp)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(4.dp, shape)
            .clip(shape)
            .background(if (state.bet) ShowGold else PaperCard)
            .border(3.dp, PaperEdge, shape)
            .clickable(enabled = enabled, role = Role.Switch, onClick = actions.onBet)
            .alpha(if (enabled) 1f else 0.5f)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(stringResource(R.string.show_bet), color = Ink, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, modifier = Modifier.weight(1f))
            Text(if (state.bet) "✓" else "", color = Ink, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold)
        }
        Text(stringResource(if (enabled) R.string.show_bet_hint else R.string.show_bet_used), color = Ink.copy(alpha = 0.75f), fontSize = 13.sp, lineHeight = 18.sp)
    }
}

@Composable
internal fun PaperPanel(modifier: Modifier = Modifier, content: @Composable ColumnScope.() -> Unit) {
    val shape = RoundedCornerShape(16.dp)
    Column(
        modifier = modifier
            .fillMaxWidth()
            .shadow(4.dp, shape)
            .clip(shape)
            .background(PaperCard)
            .border(3.dp, PaperEdge, shape)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp),
        content = content,
    )
}

@Composable
private fun Countdown(seconds: Int) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(stringResource(R.string.game_starts_in), color = AppTheme.color.textSecondaryColor, fontSize = 14.sp)
            Text("$seconds", color = AppTheme.color.textColor, fontSize = 72.sp, fontWeight = FontWeight.ExtraBold)
        }
    }
}

/** A burning rope: the fire eats it from the right, a freeze turns it blue; after «Я первый» - the time for the answer */
@Composable
private fun FuseBar(state: ShowViewModel.State, now: Long) {
    val answered = state.myAnswer
    val fuse: Fuse = state.answerFuse ?: state.fuse ?: return
    val left = if (answered != null) 0L else fuse.left(now)
    val share = left.toFloat() / fuse.lengthMs
    val frozen = answered == null && fuse.isFrozen(now)
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        Canvas(Modifier.weight(1f).height(14.dp)) {
            val radius = CornerRadius(size.height / 2)
            drawRoundRect(LineColor, cornerRadius = radius)
            if (share > 0f) {
                val width = size.width * share
                drawRoundRect(
                    brush = if (frozen) Brush.horizontalGradient(listOf(Ice, Color.White)) else Brush.horizontalGradient(listOf(ShowGold, Burn)),
                    size = Size(width, size.height),
                    cornerRadius = radius,
                )
                drawCircle(if (frozen) Color.White else Color(0xFFFFE08A), radius = size.height * 0.75f, center = Offset(width, size.height / 2))
            }
        }
        val seconds = ((left + 999) / 1000).toInt()
        val label = when {
            answered != null -> resultText(answered, state.match.roundComplete, state)
            frozen -> stringResource(R.string.show_fuse_frozen)
            state.answerFuse != null -> stringResource(R.string.show_answer_fuse, seconds)
            else -> stringResource(R.string.show_fuse, seconds)
        }
        Text(label, color = if (answered != null) pointsColor(answered.points) else AppTheme.color.textSecondaryColor, fontSize = 13.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun resultText(answer: RoundAnswer, complete: Boolean, state: ShowViewModel.State): String {
    val waiting = state.match.players.filter { it.id != state.meId && it.id !in state.match.gone && state.match.answerOf(it.id) == null }
    return if (!complete && waiting.isNotEmpty()) {
        stringResource(R.string.show_waiting, waiting.joinToString { it.name })
    } else {
        signed(answer.points)
    }
}

/** 50 на 50 and the freeze, once a show each */
@Composable
private fun PowerUps(state: ShowViewModel.State, actions: ShowActions) {
    val question = state.question
    val open = state.phase == ShowViewModel.State.Phase.QUESTION && state.myAnswer == null
    val fiftyFits = question is ShowQuestion.Pick && state.canAnswer
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        PowerUp(stringResource(R.string.show_fifty, if (state.fiftyUsed) 0 else 1), enabled = open && fiftyFits && !state.fiftyUsed, Modifier.weight(1f), actions.onFifty)
        PowerUp(stringResource(R.string.show_freeze, if (state.freezeUsed) 0 else 1), enabled = open && !state.freezeUsed, Modifier.weight(1f), actions.onFreeze)
    }
}

@Composable
private fun PowerUp(text: String, enabled: Boolean, modifier: Modifier, onClick: () -> Unit) {
    val shape = RoundedCornerShape(12.dp)
    Text(
        text = text,
        color = if (enabled) Ink else DimColor,
        fontSize = 14.sp,
        fontWeight = FontWeight.Bold,
        textAlign = TextAlign.Center,
        modifier = modifier
            .clip(shape)
            .background(if (enabled) PaperCard else Color.Transparent)
            .border(1.dp, if (enabled) PaperEdge else LineColor, shape)
            .clickable(enabled = enabled, role = Role.Button, onClick = onClick)
            .padding(vertical = 12.dp),
    )
}

internal fun ShowKind.title(): Int = when (this) {
    ShowKind.MANA -> R.string.show_kind_mana
    ShowKind.HERO -> R.string.show_kind_hero
    ShowKind.VOICE -> R.string.show_kind_voice
    ShowKind.COMPARE -> R.string.show_kind_compare
    ShowKind.BUILD -> R.string.show_kind_build
}

private fun ShowKind.hint(): Int = when (this) {
    ShowKind.MANA -> R.string.show_hint_mana_intro
    ShowKind.HERO -> R.string.show_hint_hero
    ShowKind.VOICE -> R.string.show_hint_voice
    ShowKind.COMPARE -> R.string.show_hint_compare
    ShowKind.BUILD -> R.string.show_hint_build
}

internal fun pointsColor(value: Int): Color = when {
    value > 0 -> Color(0xFF7FD08A)
    value < 0 -> Color(0xFFFF8A80)
    else -> DimColor
}

private val previewHeroes = listOf("Pudge", "Axe", "Lion", "Zeus").map { ShowOption(it, "file:///android_asset/heroes/${it.lowercase()}/full.webp", isRight = it == "Axe") }

internal fun previewState(phase: ShowViewModel.State.Phase = ShowViewModel.State.Phase.QUESTION): ShowViewModel.State {
    val players = listOf(ShowPlayer("me", "Me"), ShowPlayer("p", "Pixel 8"))
    val rounds = ShowPlan.create(1)
    val match = ShowMatch(players, rounds, round = 2, answers = mapOf(0 to mapOf("me" to RoundAnswer(true, 180), "p" to RoundAnswer(true, 150)), 1 to mapOf("me" to RoundAnswer(true, 300), "p" to RoundAnswer(false, 0)), 2 to mapOf("p" to RoundAnswer(true, 160))))
    return ShowViewModel.State.start(GameMode.VERSUS, "me", match).copy(
        phase = phase,
        question = ShowQuestion.Pick(ShowKind.VOICE, spellImage = null, spellLevel = 0, voice = null, options = previewHeroes),
        voice = Voice.READY,
        fuse = Fuse(startedAt = 0, lengthMs = 12_000),
        bet = true,
    )
}

@Preview(backgroundColor = 0xFF202225, showBackground = true, widthDp = 390, heightDp = 844)
@Composable
private fun ShowQuestionPreview() {
    AppTheme(isDarkTheme = true) {
        ShowScreen(previewState(), ShowActions(), clock = { 5_000 })
    }
}

@Preview(backgroundColor = 0xFF202225, showBackground = true, widthDp = 390, heightDp = 844)
@Composable
private fun ShowIntroPreview() {
    AppTheme(isDarkTheme = true) {
        ShowScreen(previewState(ShowViewModel.State.Phase.INTRO).copy(question = null, bet = false), ShowActions(), clock = { 0 })
    }
}
