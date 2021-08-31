package n7.ad2.ui.splash.domain.usecase

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import n7.ad2.data.source.local.AppPreference
import javax.inject.Inject

class SaveCurrentDateUseCase @Inject constructor(
    private val ioDispatcher: CoroutineDispatcher,
    private val preferences: AppPreference,
    private val getCurrentDateUseCase: GetCurrentDateInYearUseCase,
) {

    suspend operator fun invoke() = withContext(ioDispatcher) {
        preferences.saveDate(getCurrentDateUseCase.invoke())
    }

}