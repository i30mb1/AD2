package n7.ad2.ui.splash

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import n7.ad2.ui.splash.domain.interactor.ShouldWeShowSplashScreenInteractor
import n7.ad2.ui.splash.domain.usecase.GetRandomEmoteUseCase
import n7.ad2.ui.splash.domain.usecase.LoadNewsUseCase
import n7.ad2.ui.splash.domain.usecase.SaveCurrentDateUseCase
import javax.inject.Inject

class SplashViewModel @Inject constructor(
    application: Application,
    private val saveCurrentDateUseCase: SaveCurrentDateUseCase,
    private val getRandomEmoteUseCase: GetRandomEmoteUseCase,
    private val shouldWeShowSplashScreenInteractor: ShouldWeShowSplashScreenInteractor,
    private val loadNewsUseCase: LoadNewsUseCase,
) : AndroidViewModel(application) {

    init {
        viewModelScope.launch { saveCurrentDateUseCase() }
    }

    val textEmote: LiveData<String> = liveData {
        emit(getRandomEmoteUseCase())
    }

    suspend fun shouldWeShowSplashScreen(): Boolean = shouldWeShowSplashScreenInteractor()

    fun loadNews() = loadNewsUseCase()

}