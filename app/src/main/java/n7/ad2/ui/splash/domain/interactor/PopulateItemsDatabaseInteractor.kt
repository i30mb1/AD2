package n7.ad2.ui.splash.domain.interactor

import n7.ad2.ui.splash.domain.usecase.ConvertAssetsItemListToLocalItemListUseCase
import n7.ad2.ui.splash.domain.usecase.ConvertJsonItemsToAssetsItemUseCase
import n7.ad2.ui.splash.domain.usecase.GetJsonItemsFromAssetsUseCase
import n7.ad2.ui.splash.domain.usecase.SaveLocalItemsInDatabaseUseCase
import javax.inject.Inject

class PopulateItemsDatabaseInteractor @Inject constructor(
    private val getJsonItemsFromAssetsUseCase: GetJsonItemsFromAssetsUseCase,
    private val convertJsonItemsToAssetsItemUseCase: ConvertJsonItemsToAssetsItemUseCase,
    private val convertAssetsItemListToLocalItemListUseCase: ConvertAssetsItemListToLocalItemListUseCase,
    private val saveLocalItemsInDatabaseUseCase: SaveLocalItemsInDatabaseUseCase
) {

    suspend operator fun invoke() {
        val json = getJsonItemsFromAssetsUseCase()
        val assetsItemsList = convertJsonItemsToAssetsItemUseCase(json)

        val localItemsList = convertAssetsItemListToLocalItemListUseCase(assetsItemsList)
        saveLocalItemsInDatabaseUseCase(localItemsList)
    }

}