package n7.ad2.microbenchmark

import androidx.benchmark.junit4.BenchmarkRule
import androidx.benchmark.junit4.measureRepeated
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Benchmark, which will execute on an Android device.
 *
 * The body of [BenchmarkRule.measureRepeated] is measured in a loop, and Studio will
 * output the result. Modify your code to see how it affects performance.
 */
@RunWith(AndroidJUnit4::class)
class ExampleBenchmark {

    @get:Rule
    val benchmarkRule = BenchmarkRule()

    @Test
    fun testSimple() {
        val scope = CoroutineScope(Job())
        val state = MutableStateFlow<List<Int>>(emptyList())
        benchmarkRule.measureRepeated {
            scope.launch {
                massiveRun {
                    val oldList = state.value
                    val newList = oldList.toMutableList()
                    newList.add(it)
                    state.value = newList
                }
            }
        }
    }

    @Test
    fun testMutex() {
        val scope = CoroutineScope(Job())
        val state = MutableStateFlow<List<Int>>(emptyList())
        benchmarkRule.measureRepeated {
            scope.launch {
                val mutex = Mutex()
                massiveRun {
                    mutex.withLock {
                        val oldList = state.value
                        val newList = oldList.toMutableList()
                        newList.add(it)
                        state.value = newList
                    }
                }
            }
        }
    }

    private suspend fun massiveRun(action: suspend (int: Int) -> Unit) {
        coroutineScope {
            repeat(1) { int ->
                launch {
                    repeat(1) { action(int) }
                }
            }
        }
    }

}