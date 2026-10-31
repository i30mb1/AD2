package n7.ad2.games.internal.games.mid.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameMillis
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import n7.ad2.core.ui.compose.AppTheme
import n7.ad2.feature.games.mix.ui.R
import n7.ad2.games.internal.compose.DimColor
import n7.ad2.games.internal.compose.MeColor
import n7.ad2.games.internal.compose.PeerColor
import n7.ad2.games.internal.games.mid.BotLevel
import n7.ad2.games.internal.games.mid.Feedback
import n7.ad2.games.internal.games.mid.LaneSim
import n7.ad2.games.internal.games.mid.MidHero
import n7.ad2.games.internal.games.mid.MidViewModel
import n7.ad2.games.internal.games.mid.Side
import n7.ad2.games.room.GameMode

@Immutable
internal class MidActions(
    val onPickHero: (MidHero) -> Unit = {},
    val onPickLevel: (BotLevel) -> Unit = {},
    val onPickTraining: () -> Unit = {},
    val onReady: () -> Unit = {},
    val onCreepTap: (creepId: Int) -> Unit = {},
    val onHeroTap: () -> Unit = {},
    val onAgain: () -> Unit = {},
    val onOtherGame: () -> Unit = {},
)

@Composable
internal fun MidScreen(state: MidViewModel.State, sim: LaneSim, elapsedMs: () -> Long, countdown: () -> Int, actions: MidActions) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppTheme.color.background)
            .systemBarsPadding()
            .padding(horizontal = 12.dp, vertical = 10.dp),
    ) {
        when (state.phase) {
            MidViewModel.State.Phase.PICK -> MidPick(state, actions)
            MidViewModel.State.Phase.PLAYING -> MidPlay(state, sim, elapsedMs, countdown, actions)
            MidViewModel.State.Phase.END -> MidEnd(state, actions)
        }
    }
}

@Composable
private fun ColumnScope.MidPlay(state: MidViewModel.State, sim: LaneSim, elapsedMs: () -> Long, countdown: () -> Int, actions: MidActions) {
    // a frame clock: creeps, health and hits are pure functions of the match time
    var now by remember { mutableLongStateOf(elapsedMs()) }
    var secondsToStart by remember { mutableIntStateOf(countdown()) }
    LaunchedEffect(Unit) {
        while (true) {
            withFrameMillis {
                now = elapsedMs()
                secondsToStart = countdown()
            }
        }
    }
    Hud(state, sim, now)
    Box(Modifier.weight(1f).padding(vertical = 10.dp), contentAlignment = Alignment.Center) {
        MidField(state, sim, now, elapsedMs, actions, Modifier.fillMaxSize())
        if (secondsToStart > 0) Countdown(secondsToStart)
    }
    FeedbackBar(state.feedback)
}

@Composable
private fun Hud(state: MidViewModel.State, sim: LaneSim, now: Long) {
    val match = state.match
    val wave = (sim.creeps.lastOrNull { it.spawnAt <= now }?.wave ?: 0) + 1
    val seconds = (now.coerceAtLeast(0) / 1000).toInt()
    val myLabel = when (state.mode) {
        GameMode.COOP -> stringResource(R.string.mid_team, state.peerName.orEmpty())
        else -> stringResource(R.string.mid_you)
    }
    val opponentLabel = when {
        state.training -> stringResource(R.string.mid_training)
        state.mode == GameMode.VERSUS -> state.peerName.orEmpty()
        else -> state.botHero?.name.orEmpty()
    }
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        HudSide(state.myHero, myLabel, match.lastHits(state.mySide), match.denies(state.mySide), MeColor, alignEnd = false, Modifier.weight(1f))
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(stringResource(R.string.mid_wave, wave.coerceAtMost(LaneSim.WAVES), LaneSim.WAVES), color = DimColor, fontSize = 11.sp)
            Text("%d:%02d".format(seconds / 60, seconds % 60), color = AppTheme.color.textColor, fontSize = 15.sp, fontWeight = FontWeight.Bold)
        }
        if (state.training) {
            Text(opponentLabel, color = DimColor, fontSize = 13.sp, modifier = Modifier.weight(1f), textAlign = androidx.compose.ui.text.style.TextAlign.End)
        } else {
            HudSide(state.opponentHero, opponentLabel, match.lastHits(state.opponentSide), match.denies(state.opponentSide), PeerColor, alignEnd = true, Modifier.weight(1f))
        }
    }
}

/** A side in the header: hero portrait, name, last hits and denies */
@Composable
private fun HudSide(hero: MidHero?, label: String, lastHits: Int, denies: Int, color: Color, alignEnd: Boolean, modifier: Modifier) {
    Row(modifier, verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp, if (alignEnd) Alignment.End else Alignment.Start)) {
        if (!alignEnd) HeroAvatar(hero, color)
        Column(horizontalAlignment = if (alignEnd) Alignment.End else Alignment.Start) {
            Text(label, color = DimColor, fontSize = 11.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Row(verticalAlignment = Alignment.Bottom) {
                Text("$lastHits", color = AppTheme.color.textColor, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold)
                Text(" / $denies", color = DimColor, fontSize = 13.sp, fontWeight = FontWeight.Bold)
            }
        }
        if (alignEnd) HeroAvatar(hero, color)
    }
}

@Composable
internal fun HeroAvatar(hero: MidHero?, ring: Color, size: Int = 38) {
    AsyncImage(
        model = hero?.imageUrl,
        contentDescription = hero?.name,
        contentScale = ContentScale.Crop,
        modifier = Modifier
            .size(size.dp)
            .clip(CircleShape)
            .background(Color(0xFF2F3136))
            .border(2.dp, ring, CircleShape),
    )
}

@Composable
private fun Countdown(seconds: Int) {
    Box(
        Modifier.size(88.dp).clip(CircleShape).background(AppTheme.color.background.copy(alpha = 0.85f)).border(4.dp, MeColor, CircleShape),
        contentAlignment = Alignment.Center,
    ) {
        Text("$seconds", color = AppTheme.color.textColor, fontSize = 40.sp, fontWeight = FontWeight.Bold)
    }
}

/** What happened to the last hit: early or late and by how much, that is how the timing is learned */
@Composable
private fun FeedbackBar(feedback: Feedback?) {
    val text = when (feedback) {
        is Feedback.LastHit -> stringResource(R.string.mid_fb_last_hit, feedback.gold)
        Feedback.Deny -> stringResource(R.string.mid_fb_deny)
        is Feedback.Early -> stringResource(R.string.mid_fb_early, seconds(feedback.byMs), feedback.hp)
        is Feedback.Late -> stringResource(R.string.mid_fb_late, seconds(feedback.byMs))
        is Feedback.Taken -> stringResource(R.string.mid_fb_taken, feedback.by)
        Feedback.Canceled -> stringResource(R.string.mid_fb_canceled)
        null -> null
    }
    val good = feedback is Feedback.LastHit || feedback is Feedback.Deny
    Column(
        Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp)).background(PaperCard).padding(horizontal = 12.dp, vertical = 10.dp),
        verticalArrangement = Arrangement.spacedBy(3.dp),
    ) {
        if (text != null) Text(text, color = if (good) InkGood else Ink, fontSize = 14.sp, fontWeight = FontWeight.Bold)
        Text(stringResource(R.string.mid_hint), color = InkDim, fontSize = 12.sp)
    }
}

internal fun seconds(ms: Long): String = "%.2f".format(ms / 1000f)

internal val PaperCard = Color(0xFFF1E7D3)
internal val Ink = Color(0xFF2B2622)
internal val InkDim = Color(0xFF6B5F53)
internal val InkGood = Color(0xFF2F6B36)
internal val RadiantColor = Color(0xFF5FAE5C)
internal val DireColor = Color(0xFFD9483B)

internal fun Side.color(): Color = if (this == Side.RADIANT) RadiantColor else DireColor

private val previewHeroes = listOf(
    MidHero("Juggernaut", "Juggernaut", "", damage = 55, attackPointMs = 330, attackRateMs = 1_400, projectileSpeed = 0),
    MidHero("Sniper", "Sniper", "", damage = 43, attackPointMs = 170, attackRateMs = 1_700, projectileSpeed = 3_000),
    MidHero("Shadow Fiend", "Shadow Fiend", "", damage = 44, attackPointMs = 500, attackRateMs = 1_600, projectileSpeed = 1_200),
    MidHero("Lina", "Lina", "", damage = 53, attackPointMs = 750, attackRateMs = 1_600, projectileSpeed = 1_000),
)

private fun previewState(phase: MidViewModel.State.Phase, mode: GameMode = GameMode.SOLO): MidViewModel.State {
    val sim = LaneSim(seed = 4)
    val dire = sim.creeps.filter { it.side == Side.DIRE && it.wave == 0 }
    val radiant = sim.creeps.filter { it.side == Side.RADIANT && it.wave == 0 }
    val match = n7.ad2.games.internal.games.mid.MidMatch()
        .claim(dire[0], Side.RADIANT, 9_000)
        .claim(radiant[1], Side.DIRE, 9_500)
    return MidViewModel.State.start(mode, Side.RADIANT, peerName = "Pixel 8").copy(
        phase = phase,
        heroes = previewHeroes,
        myHero = previewHeroes[0],
        botHero = previewHeroes[1],
        match = match,
        myAttack = n7.ad2.games.internal.games.mid.Attack(dire[2].id, startAt = 11_800, releaseAt = 12_130, landAt = 12_130),
        feedback = Feedback.Early(byMs = 120, hp = 78),
        stats = n7.ad2.games.internal.games.mid.MidStats(attacks = 20, taken = 13, early = 4, late = 3, errorMs = 1_400),
    )
}

@androidx.compose.ui.tooling.preview.Preview(widthDp = 390, heightDp = 844)
@Composable
private fun MidPlayPreview() {
    AppTheme(isDarkTheme = true) {
        MidScreen(previewState(MidViewModel.State.Phase.PLAYING), LaneSim(seed = 4), elapsedMs = { 12_000L }, countdown = { 0 }, actions = MidActions())
    }
}

@androidx.compose.ui.tooling.preview.Preview(widthDp = 390, heightDp = 844)
@Composable
private fun MidPickPreview() {
    AppTheme(isDarkTheme = true) {
        MidScreen(previewState(MidViewModel.State.Phase.PICK), LaneSim(seed = 4), elapsedMs = { 0L }, countdown = { 0 }, actions = MidActions())
    }
}

@androidx.compose.ui.tooling.preview.Preview(widthDp = 390, heightDp = 844)
@Composable
private fun MidEndPreview() {
    AppTheme(isDarkTheme = true) {
        MidScreen(previewState(MidViewModel.State.Phase.END, GameMode.VERSUS), LaneSim(seed = 4), elapsedMs = { 0L }, countdown = { 0 }, actions = MidActions())
    }
}
