package n7.ad2.games.internal.games.skillmp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.retryWhen

class SkillGameViewModel @AssistedInject constructor(
    private val getRandomSkillUseCase: GetRandomSkillUseCase,
) : ViewModel() {

    @AssistedFactory
    interface Factory {
        fun create(): SkillGameViewModel
    }

    val state = MutableStateFlow<State>(State.Loading)
    private val actions = MutableStateFlow<Action>(Action.LoadQuestion)
    private val actionsJob: Job = actions
        .filter { it is Action.LoadQuestion }
        .flatMapLatest {
            getRandomSkillUseCase()
                .onStart {
                    state.value = State.Loading
                    delay(1_000)
                }
                .onEach { data ->
                    state.value = State.Data(data)
                    actions.value = Action.NoAction
                }
        }
        .retryWhen { _, attempt -> attempt <= 5 }
        .catch { state.value = State.Error }
        .launchIn(viewModelScope)

    fun loadQuestion() {
        actions.value = Action.LoadQuestion
    }

    sealed class Action {
        object LoadQuestion : Action()
        object NoAction : Action()
    }

    sealed class State {
        data class Data(val data: GetRandomSkillUseCase.Data) : State()
        object Loading : State()
        object Error : State()
    }

}