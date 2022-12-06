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
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
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
import androidx.compose.ui.layout.ContentScale
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
    onVariantClick: (spell: SkillGameViewModel.Spell) -> Unit,
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
                SpellImage(
                    modifier = Modifier.align(Alignment.Center),
                    state.spellImage,
                    state.spellLVL,
                ) { counter++ }
                VariantBlocks(
                    counter,
                    state.spellList,
                    state.showRightAnswer,
                    state.selectedSpell,
                    onVariantClick,
                    Modifier.align(Alignment.BottomCenter)
                )
            }

        }
    }
}

@Composable
private fun SpellImage(
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
            .scale(scale)
            .size(120.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        SpellImage(spellImage, onSpellClick)
        SpellLVL(spellLVL)
    }
}

@Composable
private fun ColumnScope.SpellImage(
    spellImage: String?,
    onSpellClick: () -> Unit,
) {
    val offsetY = remember { Animatable(0f) }
    val offsetX = remember { Animatable(0f) }
    val scope = rememberCoroutineScope()
    AsyncImage(
        model = spellImage,
        contentDescription = null,
        contentScale = ContentScale.Crop,
        modifier = Modifier
            .fillMaxSize()
            .offset { IntOffset(offsetX.value.roundToInt(), offsetY.value.roundToInt()) }
            .clip(RoundedCornerShape(8.dp))
            .weight(1f)
            .clickable { onSpellClick() }
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
    )
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

@Composable
private fun VariantBlocks(
    counter: Int,
    spellList: SkillGameViewModel.SpellList,
    showRightAnswer: Boolean,
    selectedSpell: SkillGameViewModel.Spell?,
    onVariantClick: (spell: SkillGameViewModel.Spell) -> Unit,
    modifier: Modifier = Modifier,
) {
    val movableBlock =
        movableContentOf {
            for (spell in spellList.list) {
                VariantBlock(spell, showRightAnswer, spell == selectedSpell, onVariantClick)
            }
        }

    when (counter % 3) {
        0 -> Row(modifier) { movableBlock() }
        1 -> Column(modifier) { movableBlock() }
        2 -> Box(modifier) { movableBlock() }
    }
}

@Composable
private fun VariantBlock(
    spell: SkillGameViewModel.Spell,
    showRightAnswer: Boolean,
    isSelected: Boolean,
    onVariantClick: (spell: SkillGameViewModel.Spell) -> Unit,
) {
    val isSmall by remember { mutableStateOf(isSelected) }
//    isSmall = showRightAnswer && spell.isRightAnswer
    val backgroundColor = if (showRightAnswer) AppTheme.color.primary else AppTheme.color.surface
    val scale by animateFloatAsState(targetValue = if (isSmall) 0.7f else 1f)
    Button(
        modifier = Modifier
            .sizeIn(45.dp, 45.dp)
            .padding(4.dp, 2.dp)
            .scale(scale)
            .clip(AppTheme.shape.small),
        colors = ButtonDefaults.buttonColors(backgroundColor = backgroundColor),
        onClick = { onVariantClick(spell) },
    ) {
        Box {
            Text(
                text = spell.cost,
                maxLines = 1,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .align(Alignment.Center),
            )
        }
    }
}