@file:OptIn(ExperimentalTime::class)

import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.time.Duration.Companion.seconds
import kotlin.time.ExperimentalTime

val crew = flowOf("Luffy", "Chopper", "Zoro", "Nami")
val scope = CoroutineScope(Job())

suspend fun main() {

    delay(10.seconds)
}


inline fun CoroutineScope.launchSave(
    context: CoroutineContext = EmptyCoroutineContext,
    crossinline onError: (Throwable) -> Unit,
    crossinline block: suspend () -> Unit,
): Job {
    val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        onError(Exception(throwable))
    }
    return launch(exceptionHandler + context) { block() }
}