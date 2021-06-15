package n7.ad2.ui.heroGuide.domain.usecase

import android.content.SharedPreferences
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GetLastDayHeroGuidesLoadedUseCase @Inject constructor(
    private val ioDispatcher: CoroutineDispatcher,
    private val preferences: SharedPreferences,
) {

    suspend operator fun invoke(key: String): Int = withContext(ioDispatcher) {
        preferences.getInt(key, 0)
    }
}