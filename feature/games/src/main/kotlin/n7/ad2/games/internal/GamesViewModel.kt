package n7.ad2.games.internal

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import n7.ad2.Resources
import n7.ad2.games.R
import n7.ad2.games.internal.data.GameVO

internal class GamesViewModel @AssistedInject constructor(
    private val res: Resources,
) : ViewModel() {

    @AssistedFactory
    interface Factory {
        fun create(): GamesViewModel
    }

    val games: LiveData<List<GameVO>> = liveData {
        emit(listOf(
            GameVO.SpellCost(res.getString(R.string.spell_cost)),
            GameVO.Apm(res.getString(R.string.apm)),
            GameVO.CanYouBuyIt(res.getString(R.string.can_you_buy_it)),
        ))
    }

}