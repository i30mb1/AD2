package n7.ad2.heroes.ui.api

import android.app.Application
import n7.ad2.Resources
import n7.ad2.app.logger.Logger
import n7.ad2.coroutines.DispatchersProvider
import n7.ad2.dagger.Dependencies
import n7.ad2.heroes.domain.usecase.GetHeroesUseCase
import n7.ad2.heroes.domain.usecase.UpdateStateViewedForHeroUseCase
import n7.ad2.navigator.Navigator

interface HeroesDependencies : Dependencies {
    val application: Application
    val res: Resources
    val navigator: Navigator
    val logger: Logger
    val getHeroesUseCase: GetHeroesUseCase
    val dispatchersProvider: DispatchersProvider
    val updateStateViewedForHeroUseCase: UpdateStateViewedForHeroUseCase
}
