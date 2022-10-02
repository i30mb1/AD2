import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.system.measureTimeMillis

//suspend fun main() {
//    flow<Int> {
//        error("Error!")
//    }
//        .onCompletion { error -> println("onCompletion error=$error") }
//        .catch { error -> println("catch error=$error") }
//        .launchIn(scope)
//    delay(1_000)
//}
