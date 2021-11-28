package n7.ad2.ui.splash.domain.usecase

import kotlinx.coroutines.withContext
import n7.ad2.coroutines.DispatchersProvider
import n7.ad2.data.source.local.AppPreference
import javax.inject.Inject

class SaveCurrentDateUseCase @Inject constructor(
    private val dispatchers: DispatchersProvider,
    private val preferences: AppPreference,
    private val getCurrentDateUseCase: GetCurrentDateInYearUseCase,
) {

    suspend operator fun invoke() = withContext(dispatchers.Default) {
        preferences.saveDate(getCurrentDateUseCase.invoke())
    }

}