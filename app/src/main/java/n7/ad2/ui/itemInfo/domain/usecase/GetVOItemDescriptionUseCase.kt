package n7.ad2.ui.itemInfo.domain.usecase;

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import n7.ad2.ui.heroInfo.domain.vo.VODescription
import n7.ad2.ui.heroInfo.domain.vo.VOTitleSimple
import n7.ad2.ui.itemInfo.domain.model.LocalItemDescription
import javax.inject.Inject

class GetVOItemDescriptionUseCase @Inject constructor(
    private val ioDispatcher: CoroutineDispatcher,
) {

    @ExperimentalStdlibApi
    @Suppress("BlockingMethodInNonBlockingContext")
    suspend operator fun invoke(localItemDescription: LocalItemDescription): List<VODescription> = withContext(ioDispatcher) {
        buildList<VODescription> {
            add(VOTitleSimple(localItemDescription.name))
        }
    }
}