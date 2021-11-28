package n7.ad2.ui.splash.domain.usecase

import ad2.n7.coroutines.DispatchersProvider
import android.content.SharedPreferences
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GetLastDayShownSplashScreenUseCase @Inject constructor(
    private val dispatchers: DispatchersProvider,
    private val preferences: SharedPreferences,
) {

    suspend operator fun invoke(key: String): Int = withContext(dispatchers.IO) {
        preferences.getInt(key, 0)
    }

}