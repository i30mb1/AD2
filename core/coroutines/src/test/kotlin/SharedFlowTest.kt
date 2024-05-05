import com.google.common.truth.Truth
import kotlinx.coroutines.debug.junit4.CoroutinesTimeout
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

class SharedFlowTest {

    @get:Rule val timeout = CoroutinesTimeout.seconds(5)

    @Test
    fun `test emit`(): Unit = runTest {
        val mutableSharedFlow = MutableSharedFlow<String>()
        val list = mutableListOf<String>()

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
        val list = mutableListOf<String>()

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
        val list = mutableListOf<String>()

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
        val list = mutableListOf<String>()
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
        val list = mutableListOf<String>()

        backgroundScope.launch(UnconfinedTestDispatcher()) {
            mutableSharedFlow.toList(list)
        }

        mutableSharedFlow.tryEmit("Message1")
        mutableSharedFlow.tryEmit("Message2")
        mutableSharedFlow.tryEmit("Message3")
        mutableSharedFlow.tryEmit("Message4")
        runCurrent()
        Truth.assertThat(list).isEmpty()
    }
}
