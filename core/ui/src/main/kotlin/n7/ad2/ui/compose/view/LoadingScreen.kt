package n7.ad2.ui.compose.view

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import n7.ad2.ui.compose.AppTheme

@Preview
@Composable
fun LoadingScreen() {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxSize(),
    ) {
        LoadingAnimation()
    }
}

@Composable
fun LoadingAnimation(
    circleColor: Color = AppTheme.color.primary,
    animationDelay: Int = 3000,
) {
    val circles = listOf(
        remember { Animatable(initialValue = 0f) },
        remember { Animatable(initialValue = 0f) },
        remember { Animatable(initialValue = 0f) },
    )
    circles.forEachIndexed { index, animatable ->
        LaunchedEffect(Unit) {
            delay(animationDelay / 3L * index)
            animatable.animateTo(
                1f,
                infiniteRepeatable(
                    tween(durationMillis = animationDelay, easing = LinearEasing),
                    RepeatMode.Restart,
                )
            )
        }
    }

    Box(modifier = Modifier
        .size(200.dp)
        .background(color = Color.Transparent)
    ) {
        circles.forEach { animatable ->
            Box(
                modifier = Modifier
                    .scale(scale = animatable.value)
                    .size(200.dp)
                    .clip(CircleShape)
                    .background(circleColor.copy((1 - animatable.value)))
            )
        }
    }

}