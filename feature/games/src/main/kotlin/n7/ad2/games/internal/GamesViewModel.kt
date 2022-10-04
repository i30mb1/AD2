package n7.ad2.games.internal

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.delay
import n7.ad2.Resources
import n7.ad2.games.R
import n7.ad2.games.internal.data.GameVO
import kotlin.time.Duration.Companion.seconds

internal class GamesViewModel @AssistedInject constructor(
    private val res: Resources,
) : ViewModel() {

    @AssistedFactory
    interface Factory {
        fun create(): GamesViewModel
    }


    val state: LiveData<State> = liveData {
        emit(State.Loading)
        delay(4.seconds)
        val games = listOf(
            GameVO.SpellCost(res.getString(R.string.spell_cost)),
            GameVO.Apm(res.getString(R.string.apm)),
            GameVO.CanYouBuyIt(res.getString(R.string.can_you_buy_it)),
        )

        val data = State.Data(games)
        emit(data)
    }

    sealed class State {
        data class Data(val games: List<GameVO>) : State()
        object Loading : State()
    }

}