import com.google.common.truth.Truth
import kotlin.time.Duration.Companion.seconds
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.ticker
import kotlinx.coroutines.debug.junit4.CoroutinesTimeout
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.plus
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import n7.ad2.coroutines.CoroutineTestRule
import org.junit.Rule
import org.junit.Test

class SharedFlowTest {

    @get:Rule val timeout = CoroutinesTimeout.seconds(5)
    private val list = mutableListOf<String>()
    @get:Rule val coroutineRule = CoroutineTestRule()

    @Test
    fun `test emit`(): Unit = runTest {
        val mutableSharedFlow = MutableSharedFlow<String>()

        mutableSharedFlow
            .onEach { list.add(it) }
            .launchIn(backgroundScope + UnconfinedTestDispatcher())

        mutableSharedFlow.emit("Message1")
        mutableSharedFlow.emit("Message2")
        mutableSharedFlow.emit("Message3")
        mutableSharedFlow.emit("Message4")
        Truth.assertThat(list)
            .containsExactly(
                "Message1",
                "Message2",
                "Message3",
                "Message4",
            )
    }

    @Test
    fun `test emit2`(): Unit = runTest {
        val mutableSharedFlow = MutableSharedFlow<String>()

        backgroundScope.launch(UnconfinedTestDispatcher()) {
            mutableSharedFlow.toList(list)
        }

        mutableSharedFlow.emit("Message1")
        mutableSharedFlow.emit("Message2")
        mutableSharedFlow.emit("Message3")
        mutableSharedFlow.emit("Message4")
        Truth.assertThat(list)
            .containsExactly(
                "Message1",
                "Message2",
                "Message3",
                "Message4",
            )
    }

    @Test
    fun `test emit before collect`(): Unit = runTest {
        val mutableSharedFlow = MutableSharedFlow<String>()

        mutableSharedFlow.emit("Message1")
        mutableSharedFlow.emit("Message2")
        backgroundScope.launch(UnconfinedTestDispatcher()) {
            mutableSharedFlow.toList(list)
        }

        mutableSharedFlow.emit("Message3")
        mutableSharedFlow.emit("Message4")
        Truth.assertThat(list)
            .containsExactly(
                "Message3",
                "Message4",
            )
    }

    @Test
    fun `test launch`(): Unit = runTest {
        backgroundScope.launch { list.add("Hello") }
        runCurrent()
        Truth.assertThat(list).containsExactly("Hello")
    }

    /**
     * https://github.com/Kotlin/kotlinx.coroutines/issues/2387
     */
    @Test
    fun `test tryEmit`(): Unit = runTest {
        val mutableSharedFlow = MutableSharedFlow<String>()

        backgroundScope.launch(UnconfinedTestDispatcher()) {
            mutableSharedFlow.toList(list)
        }

        mutableSharedFlow.tryEmit("Message1")
        mutableSharedFlow.tryEmit("Message2")
        mutableSharedFlow.tryEmit("Message3")
        mutableSharedFlow.tryEmit("Message4")
        Truth.assertThat(list).isEmpty()
    }

    // из-за одного потока если в одном будет долгая операция то и другие будут ждать
    @Test
    fun asdf() = runBlocking {
        val scope = CoroutineScope(Job() + newSingleThreadContext("solo"))
        val flow = MutableSharedFlow<String>(0, 1, BufferOverflow.DROP_OLDEST)

        var count = 0
        var latestFps: Int
        ticker(1.seconds.inWholeMilliseconds)
            .consumeAsFlow()
            .onEach {
                latestFps = count
                println("streamer speed: $latestFps")
                count = 0
            }
            .launchIn(scope)
        flow<Unit> {
            while (true) {
                delay(30)
                flow.tryEmit("Unit")
                count++
            }
        }.launchIn(scope)

        flow.onEach {
            Thread.sleep(300)
        }
            .launchIn(scope)
        Thread.sleep(500_000)
    }
}
