package n7.ad2.ui.splash.domain.interactor

import n7.ad2.ui.splash.domain.usecase.ConvertJsonItemsToAssetsItemUseCase
import n7.ad2.ui.splash.domain.usecase.GetJsonItemsFromAssetsUseCase
import javax.inject.Inject

class PopulateItemsDatabaseInteractor @Inject constructor(
    private val getJsonItemsFromAssetsUseCase: GetJsonItemsFromAssetsUseCase,
    private val convertJsonItemsToAssetsItemUseCase: ConvertJsonItemsToAssetsItemUseCase
) {

    suspend operator fun invoke() {
        val json = getJsonItemsFromAssetsUseCase()
        val assetsItemList = convertJsonItemsToAssetsItemUseCase(json)
    }

}