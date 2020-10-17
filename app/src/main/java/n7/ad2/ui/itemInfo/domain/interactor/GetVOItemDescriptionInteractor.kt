package n7.ad2.ui.itemInfo.domain.interactor

import n7.ad2.data.source.local.Locale
import n7.ad2.ui.heroInfo.domain.vo.VODescription
import n7.ad2.ui.itemInfo.domain.usecase.GetItemDescriptionFromAssetsUseCase
import n7.ad2.ui.itemInfo.domain.usecase.GetLocalItemByNameUseCase
import n7.ad2.ui.itemInfo.domain.usecase.GetLocalItemDescriptionFromJsonUseCase
import n7.ad2.ui.itemInfo.domain.usecase.GetVOItemDescriptionUseCase
import javax.inject.Inject

class GetVOItemDescriptionInteractor @Inject constructor(
    private val getLocalItemByNameUseCase: GetLocalItemByNameUseCase,
    private val getItemDescriptionFromAssetsUseCase: GetItemDescriptionFromAssetsUseCase,
    private val getLocalItemDescriptionFromJsonUseCase: GetLocalItemDescriptionFromJsonUseCase,
    private val getVOItemDescriptionUseCase: GetVOItemDescriptionUseCase,
) {

    @ExperimentalStdlibApi
    @Suppress("BlockingMethodInNonBlockingContext")
    suspend operator fun invoke(itemName: String, locale: Locale): List<VODescription> {
        val localItem = getLocalItemByNameUseCase(itemName)
        val json = getItemDescriptionFromAssetsUseCase(localItem, locale)
        val localItemDescription = getLocalItemDescriptionFromJsonUseCase(json)
        return getVOItemDescriptionUseCase(localItemDescription)
    }
}