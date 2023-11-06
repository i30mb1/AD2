package n7.ad2.games.api

import android.app.Application
import n7.ad2.Resources
import n7.ad2.app.logger.Logger
import n7.ad2.coroutines.DispatchersProvider
import n7.ad2.dagger.Dependencies
import n7.ad2.heroes.domain.usecase.GetHeroByNameUseCase
import n7.ad2.heroes.domain.usecase.GetHeroDescriptionUseCase
import n7.ad2.heroes.domain.usecase.GetHeroSpellInputStreamUseCase
import n7.ad2.heroes.domain.usecase.GetHeroesUseCase

interface GamesDependencies : Dependencies {
    val application: Application
    val res: Resources
    val logger: Logger
    val dispatchersProvider: DispatchersProvider
    val getHeroesUseCase: GetHeroesUseCase
    val getHeroByNameUseCase: GetHeroByNameUseCase
    val getHeroSpellInputStreamUseCase: GetHeroSpellInputStreamUseCase
    val getHeroDescriptionUseCase: GetHeroDescriptionUseCase
}
