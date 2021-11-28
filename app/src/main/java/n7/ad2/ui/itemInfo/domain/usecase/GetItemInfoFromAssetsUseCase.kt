package n7.ad2.ui.itemInfo.domain.usecase

import ad2.n7.coroutines.DispatchersProvider
import kotlinx.coroutines.withContext
import n7.ad2.data.source.local.ItemRepository
import n7.ad2.data.source.local.Locale
import n7.ad2.data.source.local.model.LocalItem
import javax.inject.Inject

class GetItemInfoFromAssetsUseCase @Inject constructor(
    private val dispatchers: DispatchersProvider,
    private val itemRepository: ItemRepository,
) {

    suspend operator fun invoke(localItem: LocalItem, locale: Locale) = withContext(dispatchers.Default) {
        itemRepository.getItemDescription(localItem.name, locale)
    }

}