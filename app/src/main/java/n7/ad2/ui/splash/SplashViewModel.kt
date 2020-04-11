package n7.ad2.ui.splash

import android.app.Application
import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import n7.ad2.R
import n7.ad2.ui.splash.domain.usecase.GetRandomEmoteUseCase
import n7.ad2.ui.splash.domain.usecase.SaveCurrentDateInSharedPrefUseCase
import java.text.SimpleDateFormat
import java.util.*
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

}