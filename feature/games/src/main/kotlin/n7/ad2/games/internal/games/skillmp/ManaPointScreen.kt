package n7.ad2.games.internal.games.skillmp

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.forEachGesture
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.movableContentOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerEvent
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.core.math.MathUtils
import coil.compose.AsyncImage
import kotlinx.coroutines.launch
import n7.ad2.ui.compose.AppTheme
import n7.ad2.ui.compose.view.ErrorScreen
import n7.ad2.ui.compose.view.LoadingScreen
import kotlin.math.roundToInt


@Composable
internal fun ManaPointScreen(
    state: SkillGameViewModel.State,
    onVariantClick: () -> Unit,
) {
    var color by remember { mutableStateOf(Color.Transparent) }
    color = Color(state.backgroundColor).copy(alpha = 0.2f)
    val animateColor = animateColorAsState(targetValue = color, animationSpec = tween(2_000))
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = animateColor.value)
            .systemBarsPadding()
            .padding(20.dp),
    ) {
        var counter by remember { mutableStateOf(0) }
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
            state.isError -> {
                ErrorScreen()
            }
            else -> {
                SpellImage(modifier = Modifier.align(Alignment.Center), state.spellImage) { counter++ }
                VariantBlocks(counter, state.spellList, onVariantClick, Modifier.align(Alignment.BottomCenter))
            }

        }
    }
}

@Composable
private fun SpellImage(
    modifier: Modifier = Modifier,
    spellImage: String?,
    onSpellClick: () -> Unit,
) {
    val infiniteTransition = rememberInfiniteTransition()
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(tween(1000), RepeatMode.Reverse)
    )
    val offsetY = remember { Animatable(0f) }
    val offsetX = remember { Animatable(0f) }
    val scope = rememberCoroutineScope()
    Box(
        modifier = modifier
            .scale(scale)
            .offset { IntOffset(offsetX.value.roundToInt(), offsetY.value.roundToInt()) }
            .clip(RoundedCornerShape(8.dp))
            .clickable { onSpellClick() }
            .size(120.dp)
            .pointerInput(Unit) {
                forEachGesture {
                    awaitPointerEventScope {
                        awaitFirstDown()
                        do {
                            val event: PointerEvent = awaitPointerEvent()
                            event.changes.forEach { change ->
                                scope.launch {
                                    val targetY = MathUtils.clamp(offsetY.value + change.positionChange().y, -100f, 100f)
                                    val targetX = MathUtils.clamp(offsetX.value + change.positionChange().x, -100f, 100f)
                                    offsetY.snapTo(targetY)
                                    offsetX.snapTo(targetX)
                                }
                            }
                        } while (event.changes.any { it.pressed })
                        // touch released
                        scope.launch {
                            offsetY.animateTo(0f, spring(dampingRatio = Spring.DampingRatioLowBouncy, stiffness = Spring.StiffnessLow))
                            offsetX.animateTo(0f, spring(dampingRatio = Spring.DampingRatioLowBouncy, stiffness = Spring.StiffnessLow))
                        }
                    }
                }
            },
    ) {
        AsyncImage(model = spellImage, contentDescription = null, modifier = Modifier.fillMaxSize())
    }
}

@Composable
private fun VariantBlocks(
    counter: Int,
    suggestsList: SkillGameViewModel.SpellList,
    onVariantClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val movableBlock = remember {
        movableContentOf { for (suggest in suggestsList.list) VariantBlock(suggest.cost, onVariantClick) }
    }
    when (counter % 3) {
        0 -> Row(modifier) { movableBlock() }
        1 -> Column(modifier) { movableBlock() }
        2 -> Box(modifier) { movableBlock() }
    }
}

@Composable
private fun VariantBlock(text: String, onVariantClick: () -> Unit) {
    var isSmall by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(targetValue = if (isSmall) 0.7f else 1f)
    Button(
        modifier = Modifier
            .size(75.dp)
            .padding(10.dp)
            .scale(scale)
            .clip(AppTheme.shape.small),
        onClick = {
            isSmall = !isSmall
            onVariantClick()
        },
    ) {
        Box {
            Text(
                text = text,
                maxLines = 1,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .align(Alignment.Center),
            )
        }
    }
}