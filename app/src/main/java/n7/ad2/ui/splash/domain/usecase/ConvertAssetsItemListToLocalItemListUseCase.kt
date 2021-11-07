package n7.ad2.ui.splash.domain.usecase

import n7.ad2.data.source.local.model.LocalItem
import n7.ad2.ui.splash.domain.model.AssetsItem
import javax.inject.Inject

class ConvertAssetsItemListToLocalItemListUseCase @Inject constructor() {

    operator fun invoke(assetsItemList: List<AssetsItem>): List<LocalItem> {
        return assetsItemList.map {
            LocalItem(
                name = it.name,
                type = it.section,
                viewedByUser = false,
            )
        }
    }

}