package n7.ad2.ui.splash.domain.usecase

import android.content.SharedPreferences
import androidx.core.content.edit
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class SaveLastDayShownSplashScreenUseCase @Inject constructor(
    private val ioDispatcher: CoroutineDispatcher,
    private val preferences: SharedPreferences,
) {

    suspend operator fun invoke(key: String, day: Int) = withContext(ioDispatcher) {
        preferences.edit(commit = true) {
            putInt(key, day)
        }
    }

}