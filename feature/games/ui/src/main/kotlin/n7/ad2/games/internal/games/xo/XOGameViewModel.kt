package n7.ad2.games.internal.games.xo

import androidx.lifecycle.ViewModel
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

internal class XOGameViewModel @AssistedInject constructor(
) : ViewModel() {

    @AssistedFactory
    interface Factory {
        fun create(): XOGameViewModel
    }

}
