package n7.ad2.ui.splash.domain.usecase

import android.app.Application
import androidx.core.content.edit
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import n7.ad2.R
import n7.ad2.data.source.local.Repository
import javax.inject.Inject

class SaveCurrentDateInSharedPrefUseCase @Inject constructor(
        private val ioDispatcher: CoroutineDispatcher,
        private val repository: Repository,
        private val application: Application,
        private val getCurrentDateUseCase: GetCurrentDateInYearUseCase
) {

    suspend operator fun invoke() = withContext(ioDispatcher) {
        repository.getSharedPreferences().edit(true) {
            putInt(application.getString(R.string.setting_current_day), getCurrentDateUseCase.invoke())
        }
    }

}