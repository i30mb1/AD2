package n7.ad2.ui.heroGuide.domain.interactor

import n7.ad2.logger.AD2Logger
import n7.ad2.ui.heroGuide.domain.usecase.GetLastDayHeroGuidesLoadedUseCase
import n7.ad2.ui.heroGuide.domain.usecase.SaveLastDayHeroGuidesLoadedUseCase
import n7.ad2.ui.splash.domain.usecase.GetCurrentDateInYearUseCase
import javax.inject.Inject

class ShouldWeLoadNewHeroGuidesInteractor @Inject constructor(
    private val getCurrentDateUseCase: GetCurrentDateInYearUseCase,
    private val getLastDayHeroGuidesLoadedUseCase: GetLastDayHeroGuidesLoadedUseCase,
    private val saveLastDayHeroGuidesLoadedUseCase: SaveLastDayHeroGuidesLoadedUseCase,
    private val logger: AD2Logger,
) {

    companion object {
        private fun getKey(heroName: String) = "LAST_DAY_HERO_GUIDES_LOADED_$heroName"
    }

    suspend operator fun invoke(heroName: String): Boolean {
        val currentDayInYear = getCurrentDateUseCase()
        val lastDayHeroGuidesLoadedUseCase = getLastDayHeroGuidesLoadedUseCase(getKey(heroName))
        return if (lastDayHeroGuidesLoadedUseCase < currentDayInYear) {
            saveLastDayHeroGuidesLoadedUseCase(getKey(heroName), currentDayInYear)
            logger.log("we should load new guide for $heroName")
            true
        } else {
            logger.log("we have already downloaded guide for $heroName")
            false
        }
    }
}