package n7.ad2.ui.splash.domain.usecase

import android.content.SharedPreferences
import kotlinx.coroutines.withContext
import n7.ad2.coroutines.DispatchersProvider
import javax.inject.Inject

class GetLastDayShownSplashScreenUseCase @Inject constructor(
    private val dispatchers: DispatchersProvider,
    private val preferences: SharedPreferences,
) {

    suspend operator fun invoke(key: String): Int = withContext(dispatchers.IO) {
        preferences.getInt(key, 0)
    }

}