package n7.ad2.ui.splash.domain.interactor

import n7.ad2.ui.splash.domain.usecase.GetCurrentDateInYearUseCase
import n7.ad2.ui.splash.domain.usecase.GetLastDayShownSplashScreenUseCase
import n7.ad2.ui.splash.domain.usecase.SaveLastDayShownSplashScreenUseCase
import javax.inject.Inject

class ShouldWeShowSplashScreenInteractor @Inject constructor(
    private val getCurrentDateUseCase: GetCurrentDateInYearUseCase,
    private val getLastDayShownSplashScreenUseCase: GetLastDayShownSplashScreenUseCase,
    private val saveLastDayShownSplashScreenUseCase: SaveLastDayShownSplashScreenUseCase,
) {

    companion object {
        private const val LAST_DAY_SHOWN_SPLASH_SCREEN_KEY = "LAST_DAY_SHOWN_SPLASH_SCREEN_KEY"
    }

    suspend operator fun invoke(): Boolean {
        val currentDayInYear = getCurrentDateUseCase()
        val lastDayShownSplashScreen = getLastDayShownSplashScreenUseCase(LAST_DAY_SHOWN_SPLASH_SCREEN_KEY)
        if (lastDayShownSplashScreen < currentDayInYear) {
            saveLastDayShownSplashScreenUseCase(LAST_DAY_SHOWN_SPLASH_SCREEN_KEY, currentDayInYear)
            return true
        }
        return false
    }
}