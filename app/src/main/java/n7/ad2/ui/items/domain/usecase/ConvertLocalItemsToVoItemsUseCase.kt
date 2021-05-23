package n7.ad2.ui.items.domain.usecase

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import n7.ad2.data.source.local.model.LocalItem
import n7.ad2.ui.items.domain.adapter.toVOItemBody
import n7.ad2.ui.items.domain.vo.VOItem
import n7.ad2.ui.items.domain.vo.VOItemBody
import javax.inject.Inject

class ConvertLocalItemsToVoItemsUseCase @Inject constructor(
    private val ioDispatcher: CoroutineDispatcher,
) {

    suspend operator fun invoke(list: List<LocalItem>): List<VOItemBody> = withContext(ioDispatcher) {
        list.map(LocalItem::toVOItemBody)
    }
}