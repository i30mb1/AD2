package n7.ad2.camera.internal

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.testing.TestLifecycleOwner
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.launchIn
import n7.ad2.coroutines.CoroutineTestRule
import org.junit.Rule
import org.junit.Test

class ControllerTest {

    @get:Rule val coroutineRule = CoroutineTestRule()

    @Test
    fun test() {
        val lifecycle = TestLifecycleOwner()
        val controller = Test5(lifecycle)
    }
}

class Test5(private val lifecycleOwner: LifecycleOwner) {
    init {
        flow {
            repeat(50) {
                emit(it)
            }
        }.launchIn(lifecycleOwner.lifecycleScope)
    }
}

object MyLifecycle : LifecycleOwner {
    private val lifecycleRegistry = LifecycleRegistry(this)
    override val lifecycle: Lifecycle = lifecycleRegistry

    init {
    }
}
