package n7.ad2.coroutines.extension

import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

inline fun CoroutineScope.launchSave(
    context: CoroutineContext = EmptyCoroutineContext,
    crossinline onError: (Throwable) -> Unit,
    crossinline block: suspend () -> Unit,
): Job {
    val exceptionHandler = CoroutineExceptionHandler { _, throwable -> onError(Exception(throwable)) }
    return launch(exceptionHandler + context) { block() }
}