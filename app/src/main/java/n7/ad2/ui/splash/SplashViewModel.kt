package n7.ad2.ui.splash

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import kotlinx.coroutines.launch
import n7.ad2.news.NewsWorker
import n7.ad2.ui.splash.domain.interactor.ShouldWeShowSplashScreenInteractor
import n7.ad2.ui.splash.domain.usecase.GetRandomEmoteUseCase
import n7.ad2.ui.splash.domain.usecase.SaveCurrentDateUseCase
import javax.inject.Inject

class SplashViewModel @Inject constructor(
    application: Application,
    private val saveCurrentDateUseCase: SaveCurrentDateUseCase,
    private val getRandomEmoteUseCase: GetRandomEmoteUseCase,
    private val shouldWeShowSplashScreenInteractor: ShouldWeShowSplashScreenInteractor,
) : AndroidViewModel(application) {

    init {
        viewModelScope.launch { saveCurrentDateUseCase() }
    }

    val textEmote: LiveData<String> = liveData {
        emit(getRandomEmoteUseCase())
    }

    suspend fun shouldWeShowSplashScreen(): Boolean = shouldWeShowSplashScreenInteractor()

    fun loadNews() {
        val constraints = Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()
        val data = Data.Builder()
            .putBoolean(NewsWorker.DELETE_TABLE, true)
            .build()
        val worker = OneTimeWorkRequest.Builder(NewsWorker::class.java)
            .setInputData(data)
            .setConstraints(constraints)
            .build()
        WorkManager.getInstance(getApplication()).beginUniqueWork(NewsWorker.TAG, ExistingWorkPolicy.APPEND, worker).enqueue()
    }


}