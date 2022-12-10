package n7.ad2.games.internal.games.killCreep

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import n7.ad2.ktx.dpToPx
import kotlin.math.sin

@Preview
@Composable
private fun KillCreepScreenPreview() {
    Space()
}

private class Star(
    var x: Float,
    var y: Float,
    var alpha: Float,
) {
    private val initialAlpha = alpha
    fun update(value: Float) {
        val x = (value - initialAlpha).toDouble()
        val newAlpha = 0.5f + (0.5f * sin(x).toFloat())
        alpha = newAlpha
    }
}

@Composable
fun Space() {
    val infiniteTransition = rememberInfiniteTransition()
    val infiniteTransitionFloat = infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 2f * Math.PI.toFloat(),
        animationSpec = infiniteRepeatable(tween(10_000), RepeatMode.Restart)
    )
    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val width = maxWidth.value.dpToPx
        val height = maxHeight.value.dpToPx
        val stars = remember {
            buildList {
                repeat(1_000) {
                    val x = (Math.random() * width).toFloat()
                    val y = (Math.random() * height).toFloat()
                    val alpha = (Math.random() * 2.0 * Math.PI).toFloat()
                    add(Star(x, y, alpha))
                }
            }
        }
        Canvas(
            modifier = Modifier
                .fillMaxSize(),
        ) {
            for (star in stars) {
                star.update(infiniteTransitionFloat.value)
                drawCircle(
                    color = Color.White,
                    center = Offset(star.x, star.y),
                    radius = 2f,
                    alpha = star.alpha,
                )
            }

        }
    }
}