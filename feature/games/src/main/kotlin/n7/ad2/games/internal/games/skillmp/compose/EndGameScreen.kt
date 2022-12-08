package n7.ad2.games.internal.games.skillmp.compose

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview

@Preview
@Composable
fun EndGameScreenPreview() {
    EndGameScreen(5)
}

@Composable
fun EndGameScreen(
    score: Int,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Counter(score)
    }
}

private fun getTextForScore(score: Int): String {
    return when (score) {
        -6 -> "SUCK MY DATA, HUMAN!"
        -5 -> "If our lives are already written, it would take a courageous man to change the script."
        -4 -> "Stand in the ashes of a trillion dead souls and ask the ghosts if honor matters. Their silence is your answer."
        -3 -> "You are great in bed ;) …. You can sleep for days."
        -2 -> "You didn’t fall. The floor just needed a hug."
        -1 -> "SKELETON_KING PLAYER DETECTED."
        0, 1 -> "GG&WP!"
        2 -> "Are you a boy or a girl?"
        3 -> "sorry, you lose!"
        4 -> "you do not get older. you lvl up."
        5 -> {
            "DOES THIS UNIT HAVE A SOUL?"
//                soundPool.play(does_this_unit_have_a_soul, 0.6f, 0.6f, 0, 0, 1f
        }
        6 -> "A hero need not speak. When he is gone, the world will speak for him."
        7 -> {
            "UGANDA FOREVER!"
//                soundPool.play(you_are_the_most_successful, 0.6f, 0.6f, 0, 0, 1f
        }
        8 -> "You are nobody.. nobody's perfect.. therefore YOU ARE PERFECT!"
        9 -> "If anything’s possible, then is it possible that nothing’s possible?"
        10 -> {
            "YOU SUCCEEDED WHERE OTHER DO NOT."
//                soundPool.play(you_are_succeeded_where_other_did_not, 0.6f, 0.6f, 0, 0, 1f
        }
        else -> "GG&WP!"
    }
}