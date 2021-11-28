package n7.ad2.ui.heroGuide.domain.usecase

import ad2.n7.coroutines.DispatchersProvider
import android.content.SharedPreferences
import androidx.core.content.edit
import kotlinx.coroutines.withContext
import javax.inject.Inject

class SaveLastDayHeroGuidesLoadedUseCase @Inject constructor(
    private val dispatchers: DispatchersProvider,
    private val preferences: SharedPreferences,
) {

    suspend operator fun invoke(key: String, day: Int) = withContext(dispatchers.IO) {
        preferences.edit(commit = true) {
            putInt(key, day)
        }
    }
}