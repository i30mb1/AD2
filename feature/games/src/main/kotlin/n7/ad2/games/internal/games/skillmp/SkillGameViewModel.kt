package n7.ad2.games.internal.games.skillmp

import android.graphics.Color
import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.retryWhen
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import java.util.concurrent.atomic.AtomicReference
import kotlin.time.Duration.Companion.seconds

class SkillGameViewModel @AssistedInject constructor(
    private val getRandomSkillUseCase: GetRandomSkillUseCase,
) : ViewModel() {

    @AssistedFactory
    interface Factory {
        fun create(): SkillGameViewModel
    }

    private val attempts = MutableStateFlow(0L)
    private val gameData = MutableStateFlow<GetRandomSkillUseCase.Data?>(null)
    val state: StateFlow<State> = combine(attempts, gameData) { attempts: Long, data: GetRandomSkillUseCase.Data? ->
        State.init()
    }
        .stateIn(viewModelScope, SharingStarted.Eagerly, State.init())
    private val actions = MutableSharedFlow<Action>()
        .shareIn(viewModelScope, SharingStarted.Eagerly, 0)

    init {
        actions
            .flatMapLatest { action ->
                when (action) {
                    Action.LoadQuestion -> getRandomSkill()
                    Action.ShowAnswer -> showAnswer()
                }
            }
            .retryWhen { _, _ ->
                attempts.update { value -> value + 1 }
                true
            }
            .catch { lastState.value = State.Error }
            .launchIn(viewModelScope)
    }

    private fun showAnswer() = flow {
        val data = lastState.value as State.Data
        emit(data)
    }
        .catch { lastState.value = State.Error }

    private fun getRandomSkill() = getRandomSkillUseCase()
        .onStart {
            lastState.value = State.Loading()
            delay(0.2.seconds)
        }
        .onEach { data ->
            lastState.value = State.Data(data)
            actions.value = Action.NoAction
            attempts.value = 0
        }

    fun onAction(action: Action) {

    }

    sealed class Action {
        object LoadQuestion : Action()
        object ShowAnswer : Action()
    }

    @Immutable
    data class State(
        val isLoading: Boolean,
        val loadingAttempts: Long,
        val backgroundColor: Int,
        val skillImage: String?,
        val spellList: SpellList,
    ) {
        companion object {
            fun init() = State(
                true,
                0,
                Color.TRANSPARENT,
                null,
                SpellList(emptyList()),
            )
        }
    }

    @Immutable
    data class SpellList(
        val list: List<Spell>,
    )

    @Immutable
    data class Spell(
        val cost: String,
    )

    class OneShotValue<T : Any>(value: T) {
        private val value = AtomicReference(value)
        fun get(): T? = value.getAndSet(null)
    }

}