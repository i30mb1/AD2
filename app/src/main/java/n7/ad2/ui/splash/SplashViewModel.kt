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
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class SplashViewModel @Inject constructor(
        application: Application,
        private val sharedPreferences: SharedPreferences,
        private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : AndroidViewModel(application) {

    val textEmote: LiveData<String> = liveData {
        emit(getEmote())
    }

    fun saveCurrentDateInSharedPref() = viewModelScope.launch(ioDispatcher) {
        val currentDayInString = SimpleDateFormat("DDD", Locale.US).format(Calendar.getInstance().time)
        sharedPreferences.edit(true) {
            putInt(getApplication<Application>().getString(R.string.setting_current_day), currentDayInString.toInt())
        }
    }

    private fun getEmote(): String {
        val emotes = arrayOf(
                "('.')",
                "('x')",
                "(>_<)",
                "(>.<)",
                "(;-;)",
                "\\(o_o)/",
                "(O_o)",
                "(o_0)",
                "(≥o≤)",
                "(≥o≤)",
                "(·.·)",
                "(·_·)"
        )
        return emotes.random()
    }

}