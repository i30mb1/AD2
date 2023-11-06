package n7.ad2.games.internal

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import n7.ad2.Resources
import n7.ad2.games.internal.data.GameVO

internal class GamesViewModel @AssistedInject constructor(
    private val res: Resources,
) : ViewModel() {

    @AssistedFactory
    interface Factory {
        fun create(): GamesViewModel
    }

    val state: StateFlow<State> = flow {
        val games = listOf(
            GameVO.GuessSkillMana(res),
            GameVO.Apm(res),
        )
        emit(State.Data(games))
    }
        .stateIn(viewModelScope, SharingStarted.Eagerly, State.Loading)

    sealed class State {
        data class Data(val games: List<GameVO>) : State()
        object Loading : State()
    }
}
