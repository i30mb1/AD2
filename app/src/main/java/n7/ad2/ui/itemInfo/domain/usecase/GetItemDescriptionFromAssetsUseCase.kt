package n7.ad2.ui.itemInfo.domain.usecase;

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import n7.ad2.data.source.local.ItemRepository
import n7.ad2.data.source.local.Locale
import n7.ad2.data.source.local.model.LocalItem
import javax.inject.Inject

class GetItemDescriptionFromAssetsUseCase @Inject constructor(
    private val ioDispatcher: CoroutineDispatcher,
    private val itemRepository: ItemRepository
) {

    @Suppress("BlockingMethodInNonBlockContext")
    suspend operator fun invoke(localItem: LocalItem, locale: Locale) = withContext(ioDispatcher) {
        itemRepository.getItemDescription(localItem.assetsPath, locale)
    }
}