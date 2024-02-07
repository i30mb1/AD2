package n7.ad2.feature.camera.domain.impl.state

import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

internal class StateMachine(
    private val config: StateMachineConfig,
    private val scope: CoroutineScope,
) {

    private var delayJob: Job? = null
    private val _currentState: MutableStateFlow<State> = MutableStateFlow(State.Tip)
    val currentState: StateFlow<State> = _currentState.asStateFlow()

    fun init(initialState: State = State.Tip) = scope.launch {
        _currentState.value = initialState
        updateToState()
    }

    fun goToNextState() {
        delayJob?.cancel()
        nextState()
    }

    private suspend fun updateToState() {
        val delayForCurrentState = getDelayFromState()
        delayJob = scope.launch {
            delay(delayForCurrentState)
            nextState()
        }
        delayJob?.join()
        if (_currentState.value != State.Timeout) {
            updateToState()
        }
    }

    private fun nextState() {
        _currentState.value = when (_currentState.value) {
            State.Tip -> State.Timeout
            State.Timeout -> error("такого быть не может")
        }
    }

    private fun getDelayFromState(): Duration {
        return when (_currentState.value) {
            State.Tip -> config.delay
            State.Timeout -> 0.seconds
        }
    }
}

internal class StateMachineConfig(
    val delay: Duration = 5.seconds,
)

internal sealed interface State {
    data object Tip : State
    data object Timeout : State
}