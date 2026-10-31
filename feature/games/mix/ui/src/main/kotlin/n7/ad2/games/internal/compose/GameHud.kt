package n7.ad2.games.internal.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

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
