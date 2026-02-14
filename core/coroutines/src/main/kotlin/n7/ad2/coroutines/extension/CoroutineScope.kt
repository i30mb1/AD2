package n7.ad2.coroutines.extension

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

/**
 * Launches coroutines with proper exception and completion
 *
 * @param onError callback that receive error wrapped in Exception to get detailed information about where exception arrised
 * @param onComplete callback called when coroutines job complete
 */
inline fun CoroutineScope.launchSave(
    context: CoroutineContext = EmptyCoroutineContext,
    crossinline onComplete: () -> Unit = { },
    crossinline onError: (Throwable) -> Unit,
    crossinline block: suspend () -> Unit,
): Job {
    val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        onError(Exception(throwable))
    }
    return launch(exceptionHandler + context) {
        block()
    }.apply {
        invokeOnCompletion {
            onComplete()
        }
    }
}

/**
 * Checks if exception is CancellationException and if tue rethrow it
 */
fun Throwable.checkCancellation() {
    if (this is CancellationException) {
        throw this
    }
}
