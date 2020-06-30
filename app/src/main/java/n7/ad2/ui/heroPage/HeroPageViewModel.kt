package n7.ad2.ui.heroPage

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import kotlinx.coroutines.CoroutineDispatcher
import n7.ad2.ui.heroInfo.HeroInfoViewModel
import n7.ad2.ui.heroInfo.ViewModelAssistedFactory

class HeroPageViewModel @AssistedInject constructor(
        application: Application,
        @Assisted private val savedStateHandle: SavedStateHandle,
        private val ioDispatcher: CoroutineDispatcher
) : AndroidViewModel(application) {

    @AssistedInject.Factory
    interface Factory : ViewModelAssistedFactory<HeroPageViewModel>


}