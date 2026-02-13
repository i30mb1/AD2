package n7.ad2.feature.camera.domain.impl

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

internal class StateMachine(private val config: StateMachineConfig, private val scope: CoroutineScope) {

    private lateinit var delayJob: Job
    private val _currentState: MutableStateFlow<State> = MutableStateFlow(State.Tip)
    val currentState: StateFlow<State> = _currentState.asStateFlow()

    fun init(initialState: State = State.Tip) = scope.launch {
        _currentState.value = initialState
        updateToState()
    }

    fun goToNextState() {
        delayJob.cancel()
        nextState()
    }

    private fun updateToState() {
        val delayForCurrentState = getDelayFromState()
        delayJob = scope.launch {
            delay(delayForCurrentState)
            nextState()
        }
    }

    private fun nextState() {
        _currentState.value = when (_currentState.value) {
            State.Tip -> State.Dialog
            State.Dialog -> State.Timeout
            State.Timeout -> error("такого быть не может")
        }
        if (_currentState.value != State.Timeout) {
            updateToState()
        }
    }

    private fun getDelayFromState(): Duration = when (_currentState.value) {
        State.Tip -> config.delay
        State.Dialog -> config.delay
        State.Timeout -> 0.seconds
    }
}

internal class StateMachineConfig(val delay: Duration = 5.seconds)

internal sealed interface State {
    data object Tip : State
    data object Dialog : State
    data object Timeout : State
}
