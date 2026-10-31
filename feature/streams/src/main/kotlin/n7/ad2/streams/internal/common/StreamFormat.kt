package n7.ad2.streams.internal.common

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import kotlinx.coroutines.delay
import n7.ad2.feature.streams.R
import java.text.NumberFormat
import java.util.Locale

private const val THOUSAND = 1_000
private const val SHORT_FROM = 10_000
private const val SECOND = 1_000L

/** "9 841" or "48,2 тыс." */
@Composable
internal fun viewersText(viewers: Int): String = if (viewers < SHORT_FROM) {
    NumberFormat.getIntegerInstance().format(viewers)
} else {
    stringResource(R.string.streams_viewers_thousands, "%.1f".format(Locale.getDefault(), viewers.toFloat() / THOUSAND))
}

/** "5:41:12" from the start of the stream until [nowMillis], empty when the start is unknown. */
internal fun uptimeText(startedAtMillis: Long, nowMillis: Long): String {
    if (startedAtMillis <= 0) return ""
    val seconds = ((nowMillis - startedAtMillis) / SECOND).coerceAtLeast(0)
    return "%d:%02d:%02d".format(Locale.US, seconds / 3600, seconds / 60 % 60, seconds % 60)
}

/** Current time, ticking every second while it is on the screen. */
@Composable
internal fun rememberNowMillis(): Long {
    var now by remember { mutableLongStateOf(System.currentTimeMillis()) }
    LaunchedEffect(Unit) {
        while (true) {
            delay(SECOND)
            now = System.currentTimeMillis()
        }
    }
    return now
}
