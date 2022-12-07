@file:OptIn(ExperimentalTime::class)

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flowOf
import kotlin.time.Duration.Companion.seconds
import kotlin.time.ExperimentalTime

val crew = flowOf("Luffy", "Chopper", "Zoro", "Nami")
val scope = CoroutineScope(Job())

suspend fun main() {

    delay(10.seconds)
}