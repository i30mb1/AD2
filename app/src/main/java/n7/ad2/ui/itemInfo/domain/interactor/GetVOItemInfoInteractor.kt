package n7.ad2.ui.itemInfo.domain.interactor

import n7.ad2.android.Locale
import n7.ad2.ui.itemInfo.domain.usecase.GetItemInfoFromAssetsUseCase
import n7.ad2.ui.itemInfo.domain.usecase.GetLocalItemByNameUseCase
import n7.ad2.ui.itemInfo.domain.usecase.GetLocalItemInfoFromJsonUseCase
import n7.ad2.ui.itemInfo.domain.usecase.GetVOItemInfoUseCase
import n7.ad2.ui.itemInfo.domain.vo.ItemInfo
import javax.inject.Inject

class GetVOItemInfoInteractor @Inject constructor(
    private val getLocalItemByNameUseCase: GetLocalItemByNameUseCase,
    private val getItemInfoFromAssetsUseCase: GetItemInfoFromAssetsUseCase,
    private val getLocalItemInfoFromJsonUseCase: GetLocalItemInfoFromJsonUseCase,
    private val getVOItemInfoUseCase: GetVOItemInfoUseCase,
) {

    @Suppress("BlockingMethodInNonBlockingContext")
    suspend operator fun invoke(itemName: String, locale: Locale): List<ItemInfo> {
        val localItem = getLocalItemByNameUseCase(itemName)
        val json = getItemInfoFromAssetsUseCase(localItem, locale)
        val localItemDescription = getLocalItemInfoFromJsonUseCase("json")
        return getVOItemInfoUseCase(localItemDescription)
    }
}