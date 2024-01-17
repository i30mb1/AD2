package n7.ad2.feature.games.xo.domain.internal.registrator

import kotlin.time.Duration.Companion.seconds
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.runBlocking

fun main(): Unit = runBlocking {
    val flow = callbackFlow {
        hell {
            println("test")
            trySend(it)
        }
        awaitClose { println("awaitClose") }
    }
        .onEach { println(it) }
        .shareIn(this, SharingStarted.WhileSubscribed(100))

    val job = flow.launchIn(this)
    println("start")
    delay(3.seconds)
    job.cancel()
    println("exit")
}


fun hell(callback: (Int) -> Unit) {
    Thread {
        while (true) {
            callback(1)
            Thread.sleep(1.seconds.inWholeMilliseconds)
        }
    }.start()
}