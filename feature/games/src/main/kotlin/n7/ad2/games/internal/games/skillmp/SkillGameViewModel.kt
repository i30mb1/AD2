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
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.retryWhen
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.concurrent.atomic.AtomicReference
import kotlin.time.Duration.Companion.seconds

internal class SkillGameViewModel @AssistedInject constructor(
    private val getSkillsUseCase: GetSkillsUseCase,
) : ViewModel() {

    @AssistedFactory
    interface Factory {
        fun create(): SkillGameViewModel
    }

    val state: MutableStateFlow<State> = MutableStateFlow(State.init())
    private val _actions = MutableSharedFlow<Action>()
    private val actions = _actions.shareIn(viewModelScope, SharingStarted.Eagerly, 0)

    init {
        actions
            .onEach { action ->
                reduce(state.value, action)
            }
            .retryWhen { _, _ ->
                state.update { it.copy(loadingAttempts = it.loadingAttempts + 1) }
                true
            }
            .catch { state.update { it.copy(isLoading = false, isError = true) } }
            .launchIn(viewModelScope)
    }

    fun onAction(action: Action) = viewModelScope.launch {
        _actions.emit(action)
    }

    private fun reduce(oldState: State, action: Action) {
        when (action) {
            Action.LoadQuestion -> loadQuestion()
            Action.ShowAnswer -> TODO()
        }
    }

    private fun loadQuestion() = getSkillsUseCase()
        .onStart {
            state.update { it.copy(isLoading = true) }
            delay(0.2.seconds)
        }
        .onEach { data ->
            state.update { state -> state.setSkills(data) }
        }

    sealed class Action {
        object LoadQuestion : Action()
        object ShowAnswer : Action()
    }

    @Immutable
    internal data class State(
        val isLoading: Boolean,
        val loadingAttempts: Long,
        val isError: Boolean,
        val backgroundColor: Int,
        val skillImage: String?,
        val spellList: SpellList,
    ) {

        companion object {
            fun init() = State(
                isLoading = true,
                loadingAttempts = 0,
                isError = false,
                backgroundColor = Color.TRANSPARENT,
                skillImage = null,
                spellList = SpellList(emptyList()),
            )
        }

    }

    private fun State.setSkills(data: GetSkillsUseCase.Data): State {
        return copy(
            isLoading = false,
            loadingAttempts = 0,
            backgroundColor = data.backgroundColor,
            skillImage = data.skillImage,
            spellList = SpellList(
                list = data.suggestsSpellList.map { spell ->
                    Spell(spell.cost, spell.isRightAnswer)
                }
            ),
        )
    }

    @Immutable
    internal data class SpellList(
        val list: List<Spell>,
    )

    @Immutable
    internal data class Spell(
        val cost: String,
        val isRightAnswer: Boolean,
    )

    class OneShotValue<T : Any>(value: T) {
        private val value = AtomicReference(value)
        fun get(): T? = value.getAndSet(null)
    }

}