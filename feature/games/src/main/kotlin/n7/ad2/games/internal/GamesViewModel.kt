package n7.ad2.games.internal

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import n7.ad2.Resources
import n7.ad2.games.R
import n7.ad2.games.internal.data.VOGame

internal class GamesViewModel @AssistedInject constructor(
    private val res: Resources,
) : ViewModel() {

    @AssistedFactory
    interface Factory {
        fun create(): GamesViewModel
    }

    val games = liveData {
        emit(listOf(
            VOGame.SpellCost(res.getString(R.string.spell_cost)),
            VOGame.Apm(res.getString(R.string.apm)),
            VOGame.CanYouBuyIt(res.getString(R.string.can_you_buy_it)),
        ))
    }

}