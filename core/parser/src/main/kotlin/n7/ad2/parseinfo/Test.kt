import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.system.measureTimeMillis

suspend fun main() {
    run repeatBlock@{
        repeat(1000) {
            val collection = mutableListOf<Int>()
//            val collection = Collections.synchronizedCollection(mutableListOf<Int>())
            var time: Long = 0L
            massiveRun {
                time = measureTimeMillis {
                    delay(10)
                    if (!collection.contains(1)) collection.add(1)
                }

            }
            if (collection.size > 1) {
                println(collection)
                println("time : $time")
                return@repeatBlock
            }
        }
    }
}

suspend fun massiveRun(action: suspend () -> Unit) {
    coroutineScope { repeat(7) { launch { action() } } }
}