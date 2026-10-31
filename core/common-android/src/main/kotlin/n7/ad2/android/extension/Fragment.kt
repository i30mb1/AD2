package n7.ad2.android.extension

import androidx.fragment.app.Fragment
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

/**
 * Не показывает view фрагмента, пока [isReady] не завершится (но не дольше [timeout]),
 * предыдущий фрагмент остается на экране - нет пустого кадра пока грузятся данные.
 * Вызывать в onCreate, транзакция должна быть с setReorderingAllowed(true)
 */
fun Fragment.postponeEnterUntil(timeout: Duration = 500.milliseconds, isReady: suspend () -> Unit) {
}
