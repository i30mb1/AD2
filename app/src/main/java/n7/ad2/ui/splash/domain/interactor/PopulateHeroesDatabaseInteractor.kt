package n7.ad2.ui.splash.domain.interactor

import n7.ad2.ui.splash.domain.usecase.ConvertAssetsHeroListToLocalHeroListUseCase
import n7.ad2.ui.splash.domain.usecase.ConvertJsonHeroesToAssetsHeroesUseCase
import n7.ad2.ui.splash.domain.usecase.GetJsonHeroesFromAssetsUseCase
import n7.ad2.ui.splash.domain.usecase.SaveLocalHeroesInDatabaseUseCase
import javax.inject.Inject

class PopulateHeroesDatabaseInteractor @Inject constructor(
    private val getJsonHeroesFromAssetsUseCase: GetJsonHeroesFromAssetsUseCase,
    private val convertJsonHeroesToAssetsHeroesUseCase: ConvertJsonHeroesToAssetsHeroesUseCase,
    private val convertAssetsHeroListToLocalHeroListUseCase: ConvertAssetsHeroListToLocalHeroListUseCase,
    private val saveLocalHeroesInDatabaseUseCase: SaveLocalHeroesInDatabaseUseCase
) {

    suspend operator fun invoke() {
        val json: String = getJsonHeroesFromAssetsUseCase()
        val assetsHeroesList = convertJsonHeroesToAssetsHeroesUseCase(json)
        val localHeroesList = convertAssetsHeroListToLocalHeroListUseCase(assetsHeroesList)

        saveLocalHeroesInDatabaseUseCase(localHeroesList)
    }

}