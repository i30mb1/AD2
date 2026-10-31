package n7.ad2.games.internal.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import n7.ad2.core.ui.compose.AppTheme

internal val MeColor = Color(0xFFF44336)
internal val PeerColor = Color(0xFF448AFF)
internal val RightColor = Color(0xFF43A047)
internal val GoldColor = Color(0xFFFFD166)
internal val LineColor = Color(0xFF3A3D43)
internal val DimColor = Color(0xFF8E9297)

internal enum class AnswerLook { IDLE, PICKED, RIGHT, WRONG }

/** How an answer button looks: before the reveal only the own pick is marked, after it the right answer is green and a wrong pick red. */
internal fun answerLook(isRight: Boolean, isPicked: Boolean, reveal: Boolean): AnswerLook = when {
    reveal && isRight -> AnswerLook.RIGHT
    reveal && isPicked -> AnswerLook.WRONG
    !reveal && isPicked -> AnswerLook.PICKED
    else -> AnswerLook.IDLE
}

internal fun avatarLetter(name: String): String = name.trim().firstOrNull()?.uppercase() ?: "?"

/** Round avatar with the first letter of the player name. */
@Composable
internal fun Avatar(name: String, color: Color, size: Dp = 26.dp, modifier: Modifier = Modifier) {
    Box(modifier.size(size).clip(CircleShape).background(color), contentAlignment = Alignment.Center) {
        Text(avatarLetter(name), color = Color.White, fontSize = (size.value * 0.45f).sp, fontWeight = FontWeight.Bold)
    }
}

/** Solo top bar: score chip, an optional red chip (streak, misses) and the lives as dots. */
@Composable
internal fun SoloHud(score: String, extra: String?, lives: Int?, maxLives: Int, modifier: Modifier = Modifier) {
    Row(modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        HudChip(score, AppTheme.color.surface, AppTheme.color.textColor)
        if (extra != null) HudChip(extra, MeColor.copy(alpha = 0.15f), MeColor)
        Spacer(Modifier.weight(1f))
        if (lives != null) Lives(lives, maxLives)
    }
}

/** Two-player top bar: the own card on the left with a red edge, the peer on the right with a blue one, round or time in the middle. */
@Composable
internal fun DuoHud(
    me: String,
    myScore: String,
    peerName: String,
    peerScore: String,
    centerLabel: String,
    centerValue: String,
    peerAnswered: Boolean,
    modifier: Modifier = Modifier,
) {
    Row(modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        PlayerCard(me, myScore, MeColor, mirrored = false, answered = false, modifier = Modifier.weight(1f))
        Column(Modifier.widthIn(min = 56.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(centerLabel, color = DimColor, fontSize = 10.sp)
            Text(centerValue, color = AppTheme.color.textColor, fontSize = 14.sp, fontWeight = FontWeight.Bold)
        }
        PlayerCard(peerName, peerScore, PeerColor, mirrored = true, answered = peerAnswered, modifier = Modifier.weight(1f))
    }
}

@Composable
private fun PlayerCard(name: String, score: String, color: Color, mirrored: Boolean, answered: Boolean, modifier: Modifier) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(AppTheme.color.surface)
            .drawBehind {
                val edge = 3.dp.toPx()
                drawRect(color, topLeft = androidx.compose.ui.geometry.Offset(if (mirrored) size.width - edge else 0f, 0f), size = Size(edge, size.height))
            }
            .padding(horizontal = 10.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = if (mirrored) Arrangement.spacedBy(8.dp, Alignment.End) else Arrangement.spacedBy(8.dp),
    ) {
        val avatar = @Composable {
            Box(Modifier.size(30.dp)) {
                Avatar(name, color, modifier = Modifier.align(Alignment.TopStart))
                if (answered) {
                    Box(
                        Modifier.align(Alignment.BottomEnd).size(14.dp).clip(CircleShape).background(RightColor).border(2.dp, AppTheme.color.surface, CircleShape),
                        contentAlignment = Alignment.Center,
                    ) { Text("✓", color = Color.White, fontSize = 7.sp, lineHeight = 7.sp) }
                }
            }
        }
        val value = @Composable { Text(score, color = AppTheme.color.textColor, fontSize = 20.sp, fontWeight = FontWeight.Bold, maxLines = 1) }
        if (mirrored) {
            value()
            avatar()
        } else {
            avatar()
            value()
        }
    }
}

@Composable
private fun HudChip(text: String, background: Color, color: Color) {
    Text(
        text = text,
        color = color,
        fontSize = 14.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.clip(RoundedCornerShape(16.dp)).background(background).padding(horizontal = 12.dp, vertical = 6.dp),
    )
}

@Composable
private fun Lives(lives: Int, maxLives: Int) {
    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        repeat(maxLives) { index ->
            Box(Modifier.size(10.dp).clip(CircleShape).background(if (index < lives) MeColor else LineColor))
        }
    }
}

/** Thin red bar under the top bar with a line of text on each side. */
@Composable
internal fun HudBar(progress: Float, start: String, end: String, endColor: Color = DimColor, modifier: Modifier = Modifier) {
    Column(modifier.fillMaxWidth()) {
        Box(Modifier.fillMaxWidth().height(3.dp).clip(RoundedCornerShape(2.dp)).background(AppTheme.color.surface)) {
            Box(Modifier.fillMaxWidth(progress.coerceIn(0f, 1f)).height(3.dp).clip(RoundedCornerShape(2.dp)).background(MeColor))
        }
        Row(Modifier.fillMaxWidth().padding(top = 6.dp)) {
            Text(start, color = DimColor, fontSize = 11.sp)
            Spacer(Modifier.weight(1f))
            Text(end, color = endColor, fontSize = 11.sp)
        }
    }
}

@Preview(backgroundColor = 0xFF202225, showBackground = true, widthDp = 390)
@Composable
private fun HudPreview() {
    AppTheme(isDarkTheme = true) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            SoloHud(score = "9 очков", extra = "серия 4", lives = 2, maxLives = 3)
            DuoHud(me = "вы", myScore = "3", peerName = "Pixel 8", peerScore = "2", centerLabel = "раунд", centerValue = "5 / 10", peerAnswered = true)
            HudBar(progress = 0.6f, start = "раунд 5", end = "Pixel 8 ответил", endColor = PeerColor)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Avatar("A024", MeColor, 40.dp)
                Avatar("Pixel 8", PeerColor, 40.dp)
                Spacer(Modifier.width(8.dp))
            }
        }
    }
}
