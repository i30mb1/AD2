package n7.ad2.parseinfo.algorithm

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

fun main() = runBlocking {
    println("1")
    coroutineScope {
        launch { println(".") }
        launch { println("..") }
        launch { println("...") }
    }
    println("3")
}