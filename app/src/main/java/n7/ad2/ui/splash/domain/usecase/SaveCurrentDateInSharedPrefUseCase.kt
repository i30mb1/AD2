package n7.ad2.ui.splash.domain.usecase

import android.app.Application
import android.content.SharedPreferences
import androidx.core.content.edit
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import n7.ad2.R
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class SaveCurrentDateInSharedPrefUseCase @Inject constructor(
        private val ioDispatcher: CoroutineDispatcher,
        private val sharedPreferences: SharedPreferences,
        private val application: Application
) {

    suspend operator fun invoke() = withContext(ioDispatcher) {
        val currentDayInString = SimpleDateFormat("DDD", Locale.US).format(Calendar.getInstance().time)
        sharedPreferences.edit(true) {
            putInt(application.getString(R.string.setting_current_day), currentDayInString.toInt())
        }
    }

}