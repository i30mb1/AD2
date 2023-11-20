package n7.ad2.feature.games.xo.domain.internal.registrator

import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.runBlocking

fun main(): Unit = runBlocking {
    val innerFlow = flow<String> { error("innerFlow") }
    flowOf(1)
        .flatMapConcat {
            innerFlow
        }
        .catch { error -> println(error) }
        .launchIn(this)
}
