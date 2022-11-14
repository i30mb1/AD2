package n7.ad2.games.internal.games.skillmp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.retryWhen
import kotlinx.coroutines.flow.stateIn
import kotlin.time.Duration.Companion.seconds

class SkillGameViewModel @AssistedInject constructor(
    private val getRandomSkillUseCase: GetRandomSkillUseCase,
) : ViewModel() {

    @AssistedFactory
    interface Factory {
        fun create(): SkillGameViewModel
    }

    private val attempts = MutableStateFlow(0L)
    private val internalState = MutableStateFlow<State>(State.Loading(0))
    val state: StateFlow<State> = combine(attempts, internalState) { attempts: Long, internalState: State ->
        if (internalState is State.Loading) internalState.copy(attempts = attempts)
        else internalState
    }
        .stateIn(viewModelScope, SharingStarted.Eagerly, internalState.value)
    private val actions = MutableStateFlow<Action>(Action.LoadQuestion)

    init {
        actions
            .filter { it is Action.LoadQuestion }
            .flatMapLatest {
                getRandomSkillUseCase()
                    .onStart {
                        internalState.value = State.Loading()
                        delay(1.seconds)
                    }
                    .onEach { data ->
                        internalState.value = State.Data(data)
                        actions.value = Action.NoAction
                    }
            }
            .retryWhen { _, attempt ->
                attempts.value = attempt
                true
            }
            .catch { internalState.value = State.Error }
            .launchIn(viewModelScope)
    }

    fun loadQuestion() {
        actions.value = Action.LoadQuestion
    }

    sealed class Action {
        object LoadQuestion : Action()
        object NoAction : Action()
    }

    sealed class State {
        data class Data(val data: GetRandomSkillUseCase.Data) : State()
        data class Loading(val attempts: Long = 0) : State()
        object Error : State()
    }

}