package n7.ad2.ui.itemInfo.domain.interactor

import n7.ad2.data.source.local.Locale
import n7.ad2.ui.itemInfo.domain.usecase.GetItemInfoFromAssetsUseCase
import n7.ad2.ui.itemInfo.domain.usecase.GetLocalItemByNameUseCase
import n7.ad2.ui.itemInfo.domain.usecase.GetLocalItemInfoFromJsonUseCase
import n7.ad2.ui.itemInfo.domain.usecase.GetVOItemInfoUseCase
import n7.ad2.ui.itemInfo.domain.vo.ItemInfo
import n7.ad2.utils.Result
import javax.inject.Inject

class GetVOItemInfoInteractor @Inject constructor(
    private val getLocalItemByNameUseCase: GetLocalItemByNameUseCase,
    private val getItemInfoFromAssetsUseCase: GetItemInfoFromAssetsUseCase,
    private val getLocalItemInfoFromJsonUseCase: GetLocalItemInfoFromJsonUseCase,
    private val getVOItemInfoUseCase: GetVOItemInfoUseCase,
) {

    @Suppress("BlockingMethodInNonBlockingContext")
    suspend operator fun invoke(itemName: String, locale: Locale): Result<List<ItemInfo>> = try {
        val localItem = getLocalItemByNameUseCase(itemName)
        val json = getItemInfoFromAssetsUseCase(localItem, locale)
        val localItemDescription = getLocalItemInfoFromJsonUseCase(json)
        val result = getVOItemInfoUseCase(localItemDescription)
        Result.success(result)
    } catch (e: Exception) {
        Result.failure(e)
    }
}