package n7.ad2.ui

import android.view.View
import android.view.View.OnAttachStateChangeListener
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

/**
 * Метод можно использовать для запуска некоторой работы внутри CoroutineScope
 * при создании View
 * @link https://www.droidcon.com/2023/10/06/coroutines-flow-android-the-good-parts/
 */
fun View.whileAttachedOnce(
    context: CoroutineContext = EmptyCoroutineContext,
    work: suspend CoroutineScope.() -> Unit,
) {
    var scope: CoroutineScope? = null
    val launchWork = {
        scope = CoroutineScope(context)
        scope?.launch { work() }
    }
    addOnAttachStateChangeListener(object : OnAttachStateChangeListener {
        override fun onViewAttachedToWindow(v: View) {
            launchWork()
        }

        override fun onViewDetachedFromWindow(v: View) {
            scope?.cancel()
        }
    })
    if (isAttachedToWindow) launchWork()
}
