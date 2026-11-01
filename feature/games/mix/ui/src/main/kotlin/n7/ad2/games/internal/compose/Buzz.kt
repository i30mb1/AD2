package n7.ad2.games.internal.compose

import android.view.HapticFeedbackConstants
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalView

/** A short buzz of the phone at a moment of a game: something went right or wrong */
internal enum class Buzz(val feedback: Int) {
    GOOD(HapticFeedbackConstants.CONFIRM),
    BAD(HapticFeedbackConstants.REJECT),
}

/** Buzzes once [count] grows: a spell invoked, a creep taken, a board won; the first value is the start */
@Composable
internal fun BuzzOnIncrease(count: Int, buzz: Buzz) {
    val view = LocalView.current
    var last by remember { mutableIntStateOf(count) }
    LaunchedEffect(count) {
        if (count > last) view.performHapticFeedback(buzz.feedback)
        last = count
    }
}

/** Buzzes once my answer of a round is known: [correct] null - no answer yet */
@Composable
internal fun BuzzOnAnswer(correct: Boolean?) {
    val view = LocalView.current
    LaunchedEffect(correct) {
        if (correct != null) view.performHapticFeedback(if (correct) Buzz.GOOD.feedback else Buzz.BAD.feedback)
    }
}
