import com.google.common.truth.Truth
import kotlin.time.Duration.Companion.seconds
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runTest
import n7.ad2.feature.camera.domain.impl.state.State
import n7.ad2.feature.camera.domain.impl.state.StateMachine
import n7.ad2.feature.camera.domain.impl.state.StateMachineConfig
import org.junit.Test

class StateMachineTest {

    @Test
    fun `without interruption`() = runTest {
        val machine = StateMachine(StateMachineConfig(5.seconds), this)
        machine.init(State.Tip)
        Truth.assertThat(machine.currentState.value).isEqualTo(State.Tip)
        advanceTimeBy(1.seconds)
        Truth.assertThat(machine.currentState.value).isEqualTo(State.Tip)
        advanceTimeBy(5.seconds)
        Truth.assertThat(machine.currentState.value).isEqualTo(State.Timeout)
    }

    @Test
    fun `with interruption`() = runTest {
        val machine = StateMachine(StateMachineConfig(5.seconds), this)
        machine.init(State.Tip)
        Truth.assertThat(machine.currentState.value).isEqualTo(State.Tip)
        advanceTimeBy(1.seconds)
        Truth.assertThat(machine.currentState.value).isEqualTo(State.Tip)
        machine.goToNextState()
        Truth.assertThat(machine.currentState.value).isEqualTo(State.Timeout)
    }
}