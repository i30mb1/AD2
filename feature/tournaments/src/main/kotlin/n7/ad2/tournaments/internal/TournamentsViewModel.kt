package n7.ad2.tournaments.internal

import androidx.lifecycle.ViewModel
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

class TournamentsViewModel @AssistedInject constructor(
) : ViewModel() {

    @AssistedFactory
    interface Factory {
        fun create(): TournamentsViewModel
    }

}