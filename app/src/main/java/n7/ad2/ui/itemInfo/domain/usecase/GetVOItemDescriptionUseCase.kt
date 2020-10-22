package n7.ad2.ui.itemInfo.domain.usecase;

import android.app.Application
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import n7.ad2.R
import n7.ad2.ui.heroInfo.domain.vo.VOBodySimple
import n7.ad2.ui.heroInfo.domain.vo.VODescription
import n7.ad2.ui.heroInfo.domain.vo.VOTitleSimple
import n7.ad2.ui.itemInfo.domain.model.LocalItemDescription
import javax.inject.Inject

class GetVOItemDescriptionUseCase @Inject constructor(
    private val ioDispatcher: CoroutineDispatcher,
    private val application: Application
) {

    suspend operator fun invoke(localItemDescription: LocalItemDescription): List<VODescription> = withContext(ioDispatcher) {
        mutableListOf<VODescription>().apply {
            add(VOTitleSimple(localItemDescription.name))
            add(VOBodySimple(localItemDescription.description))

        }
    }
}