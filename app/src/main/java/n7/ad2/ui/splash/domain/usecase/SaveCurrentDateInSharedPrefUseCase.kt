package n7.ad2.ui.splash.domain.usecase

import android.app.Application
import android.content.SharedPreferences
import androidx.core.content.edit
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import n7.ad2.R
import javax.inject.Inject

class SaveCurrentDateInSharedPrefUseCase @Inject constructor(
        private val ioDispatcher: CoroutineDispatcher,
        private val sharedPreferences: SharedPreferences,
        private val application: Application,
        private val getCurrentDateUseCase: GetCurrentDateUseCase
) {

    suspend operator fun invoke() = withContext(ioDispatcher) {
        sharedPreferences.edit(true) {
            putInt(application.getString(R.string.setting_current_day), getCurrentDateUseCase.invoke())
        }
    }

}