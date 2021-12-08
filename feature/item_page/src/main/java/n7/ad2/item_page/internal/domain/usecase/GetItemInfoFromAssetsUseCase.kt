package n7.ad2.item_page.internal.domain.usecase

import kotlinx.coroutines.withContext
import n7.ad2.android.Locale
import n7.ad2.coroutines.DispatchersProvider
import n7.ad2.repositories.ItemRepository
import javax.inject.Inject

class GetItemInfoFromAssetsUseCase @Inject constructor(
    private val dispatchers: DispatchersProvider,
    private val itemRepository: ItemRepository,
) {

    suspend operator fun invoke(localItem: n7.ad2.database_guides.internal.model.LocalItem, locale: Locale) = withContext(dispatchers.Default) {
//        itemRepository.getItemDescription(localItem.name, locale)
    }

}