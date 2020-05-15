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
import n7.ad2.ui.splash.domain.usecase.GetRandomEmoteUseCase
import n7.ad2.ui.splash.domain.usecase.SaveCurrentDateInSharedPrefUseCase
import javax.inject.Inject

class SplashViewModel @Inject constructor(
        application: Application,
        private val saveCurrentDateInSharedPrefUseCase: SaveCurrentDateInSharedPrefUseCase,
        private val getRandomEmoteUseCase: GetRandomEmoteUseCase
) : AndroidViewModel(application) {

    val textEmote: LiveData<String> = liveData {
        emit(getRandomEmoteUseCase())
    }

    fun saveCurrentDateInSharedPref() = viewModelScope.launch {
        saveCurrentDateInSharedPrefUseCase()
    }

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