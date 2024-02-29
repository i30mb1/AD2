import com.google.common.truth.Truth
import kotlin.time.Duration.Companion.seconds
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runTest
import n7.ad2.feature.camera.domain.impl.State
import n7.ad2.feature.camera.domain.impl.StateMachine
import n7.ad2.feature.camera.domain.impl.StateMachineConfig
import org.junit.Test

class StateMachineTest {

    @Test
    fun `without interruption`() = runTest {
        val machine = StateMachine(StateMachineConfig(5.seconds), this)
        machine.init(State.Tip)
        Truth.assertThat(machine.currentState.value).isEqualTo(State.Tip)
        advanceTimeBy(1.seconds)
        Truth.assertThat(machine.currentState.value).isEqualTo(State.Tip)
        advanceTimeBy(6.seconds)
        Truth.assertThat(machine.currentState.value).isEqualTo(State.Dialog)
        advanceTimeBy(6.seconds)
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
        Truth.assertThat(machine.currentState.value).isEqualTo(State.Dialog)
        advanceTimeBy(6.seconds)
        Truth.assertThat(machine.currentState.value).isEqualTo(State.Timeout)
    }
}