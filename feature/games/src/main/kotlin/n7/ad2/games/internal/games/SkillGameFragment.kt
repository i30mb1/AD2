package n7.ad2.games.internal.games

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.Spring.StiffnessLow
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
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
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
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import kotlinx.coroutines.launch
import n7.ad2.android.findDependencies
import n7.ad2.games.internal.di.DaggerGamesComponent
import n7.ad2.games.internal.games.skillmp.GetRandomSkillUseCase
import n7.ad2.games.internal.games.skillmp.SkillGameViewModel
import n7.ad2.ktx.viewModel
import n7.ad2.ui.ComposeView
import n7.ad2.ui.compose.AppTheme
import n7.ad2.ui.compose.view.ErrorScreen
import n7.ad2.ui.compose.view.LoadingScreen
import javax.inject.Inject
import kotlin.math.roundToInt

class SkillGameFragment : Fragment() {

    companion object {
        fun getInstance(): SkillGameFragment = SkillGameFragment()
    }

    @Inject lateinit var skillGameViewModelFactory: SkillGameViewModel.Factory
    private val viewModel: SkillGameViewModel by viewModel { skillGameViewModelFactory.create() }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        DaggerGamesComponent.factory().create(findDependencies()).inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return ComposeView {
            val state: State<SkillGameViewModel.State> = viewModel.state.collectAsState()
            when (val data = state.value) {
                is SkillGameViewModel.State.Data -> GuessTheSkillManaPointScreen(data.data, viewModel::loadQuestion)
                SkillGameViewModel.State.Error -> ErrorScreen()
                SkillGameViewModel.State.Loading -> LoadingScreen()
            }
        }
    }

    @Composable
    private fun GuessTheSkillManaPointScreen(data: GetRandomSkillUseCase.Data, onBlockClicked: () -> Unit) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
                .padding(20.dp),
        ) {
            var counter by remember { mutableStateOf(0) }
            Circle(modifier = Modifier.align(Alignment.TopCenter)) { counter++ }
            Blocks(counter, data.suggestsList, onBlockClicked)
        }
    }

    @Composable
    private fun BoxScope.Blocks(
        counter: Int,
        suggestsList: List<String>,
        onBlockClicked: () -> Unit,
    ) {
        val movableBlock = remember {
            movableContentOf {
                for (suggest in suggestsList) {
                    Block(suggest, onBlockClicked)
                }
            }
        }
        when (counter % 3) {
            0 -> Row(Modifier.Companion.align(Alignment.BottomCenter)) { movableBlock() }
            1 -> Column(Modifier.Companion.align(Alignment.BottomCenter)) { movableBlock() }
            2 -> Box(Modifier.Companion.align(Alignment.BottomCenter)) { movableBlock() }
        }
    }

    @Composable
    private fun BoxScope.Block(suggest: String, onBlockClicked: () -> Unit) {
        var isSmall by remember { mutableStateOf(false) }
        val scale by animateFloatAsState(targetValue = if (isSmall) 0.7f else 1f)
        Surface(
            modifier = Modifier
                .size(75.dp)
                .padding(10.dp)
                .scale(scale)
                .background(AppTheme.color.surface)
                .clickable {
                    isSmall = !isSmall
                    onBlockClicked()
                }
        ) {
            Box {
                Text(text = suggest, modifier = Modifier.align(Alignment.Center))
            }
        }
    }

    @Composable
    fun Circle(modifier: Modifier, onCircleClicked: () -> Unit) {
        val infiniteTransition = rememberInfiniteTransition()
        val scale by infiniteTransition.animateFloat(
            initialValue = 1f,
            targetValue = 1.2f,
            animationSpec = infiniteRepeatable(tween(1000), RepeatMode.Reverse)
        )
        val offsetY = remember { Animatable(0f) }
        val scope = rememberCoroutineScope()
        Box(
            modifier = modifier
                .scale(scale)
                .offset { IntOffset(0, offsetY.value.roundToInt()) }
                .size(50.dp)
                .clip(CircleShape)
                .background(AppTheme.color.error)
                .clickable { onCircleClicked() }
                .pointerInput(Unit) {
                    forEachGesture {
                        awaitPointerEventScope {
                            awaitFirstDown()
                            do {
                                val event: PointerEvent = awaitPointerEvent()
                                event.changes.forEach { change ->
                                    scope.launch { offsetY.snapTo(offsetY.value + change.positionChange().y) }
                                }
                            } while (event.changes.any { it.pressed })
                            // touch released
                            scope.launch {
                                offsetY.animateTo(
                                    0f,
                                    spring(
                                        dampingRatio = Spring.DampingRatioLowBouncy,
                                        stiffness = StiffnessLow
                                    )
                                )
                            }
                        }
                    }
                },
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.Star, contentDescription = null, tint = Color.White)
        }
    }

//    private fun setEndText() {
//        when (score.get()) {
//            -6 -> endText.set("SUCK MY DATA, HUMAN!")
//            -5 -> endText.set("If our lives are already written, it would take a courageous man to change the script.")
//            -4 -> endText.set("Stand in the ashes of a trillion dead souls and ask the ghosts if honor matters. Their silence is your answer.")
//            -3 -> endText.set("You are great in bed ;) …. You can sleep for days.")
//            -2 -> endText.set("You didn’t fall. The floor just needed a hug.")
//            -1 -> endText.set("SKELETON_KING PLAYER DETECTED.")
//            0, 1 -> endText.set("GG&WP!")
//            2 -> endText.set("Are you a boy or a girl?")
//            3 -> {
//                endText.set("sorry, you lose!")
//                endText.set("you do not get older. you lvl up.")
//            }
//            4 -> endText.set("you do not get older. you lvl up.")
//            5 -> {
//                endText.set("DOES THIS UNIT HAVE A SOUL?")
//                soundPool.play(does_this_unit_have_a_soul, 0.6f, 0.6f, 0, 0, 1f)
//            }
//            6 -> endText.set("A hero need not speak. When he is gone, the world will speak for him.")
//            7 -> {
//                endText.set("UGANDA FOREVER!")
//                soundPool.play(you_are_the_most_successful, 0.6f, 0.6f, 0, 0, 1f)
//            }
//            8 -> endText.set("You are nobody.. nobody's perfect.. therefore YOU ARE PERFECT!")
//            9 -> endText.set("If anything’s possible, then is it possible that nothing’s possible?")
//            10 -> {
//                endText.set("YOU SUCCEEDED WHERE OTHER DO NOT.")
//                soundPool.play(you_are_succeeded_where_other_did_not, 0.6f, 0.6f, 0, 0, 1f)
//            }
//            else -> endText.set("GG&WP!")
//        }
//    }
}