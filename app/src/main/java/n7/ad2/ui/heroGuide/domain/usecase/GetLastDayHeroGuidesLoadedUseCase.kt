package n7.ad2.ui.heroGuide.domain.usecase

import android.content.SharedPreferences
import kotlinx.coroutines.withContext
import n7.ad2.coroutines.DispatchersProvider
import javax.inject.Inject

class GetLastDayHeroGuidesLoadedUseCase @Inject constructor(
    private val dispatchers: DispatchersProvider,
    private val preferences: SharedPreferences,
) {

    suspend operator fun invoke(key: String): Int = withContext(dispatchers.Default) {
        preferences.getInt(key, 0)
    }

}