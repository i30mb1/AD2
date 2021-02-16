package n7.ad2.ui.splash.domain.usecase

import android.app.Application
import android.content.SharedPreferences
import androidx.core.content.edit
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import n7.ad2.R
import javax.inject.Inject

class SaveCurrentDateUseCase @Inject constructor(
    private val ioDispatcher: CoroutineDispatcher,
    private val preferences: SharedPreferences,
    private val application: Application,
    private val getCurrentDateUseCase: GetCurrentDateInYearUseCase
) {

    suspend operator fun invoke() = withContext(ioDispatcher) {
        preferences.edit(true) {
            putInt(application.getString(R.string.setting_current_day), getCurrentDateUseCase.invoke())
        }
    }

}