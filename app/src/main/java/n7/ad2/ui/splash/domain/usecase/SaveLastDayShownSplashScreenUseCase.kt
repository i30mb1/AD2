package n7.ad2.ui.splash.domain.usecase

import ad2.n7.coroutines.DispatchersProvider
import android.content.SharedPreferences
import androidx.core.content.edit
import kotlinx.coroutines.withContext
import javax.inject.Inject

class SaveLastDayShownSplashScreenUseCase @Inject constructor(
    private val dispatchers: DispatchersProvider,
    private val preferences: SharedPreferences,
) {

    suspend operator fun invoke(key: String, day: Int) = withContext(dispatchers.Default) {
        preferences.edit(commit = true) {
            putInt(key, day)
        }
    }

}