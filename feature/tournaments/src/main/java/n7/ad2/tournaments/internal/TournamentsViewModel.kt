package n7.ad2.tournaments.internal

import android.app.Application
import androidx.lifecycle.ViewModel
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

class TournamentsViewModel @AssistedInject constructor(
    private val application: Application,
) : ViewModel() {

    @AssistedFactory
    interface Factory {
        fun create(): TournamentsViewModel
    }

}