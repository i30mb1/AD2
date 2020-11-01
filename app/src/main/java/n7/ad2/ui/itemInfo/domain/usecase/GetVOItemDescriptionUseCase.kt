package n7.ad2.ui.itemInfo.domain.usecase

import android.app.Application
import android.text.SpannableString
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import n7.ad2.R
import n7.ad2.ui.heroInfo.domain.vo.VOBodyLine
import n7.ad2.ui.heroInfo.domain.vo.VOBodyRecipe
import n7.ad2.ui.heroInfo.domain.vo.VOBodySimple
import n7.ad2.ui.heroInfo.domain.vo.VOBodyWithSeparator
import n7.ad2.ui.heroInfo.domain.vo.VODescription
import n7.ad2.ui.heroInfo.domain.vo.VOTitleSimple
import n7.ad2.ui.itemInfo.domain.adapter.toVORecipe
import n7.ad2.ui.itemInfo.domain.model.LocalItemDescription
import n7.ad2.utils.extension.toStringListWithDash
import javax.inject.Inject

class GetVOItemDescriptionUseCase @Inject constructor(
    private val ioDispatcher: CoroutineDispatcher,
    private val application: Application,
) {

    suspend operator fun invoke(localItemDescription: LocalItemDescription): List<VODescription> = withContext(ioDispatcher) {
        mutableListOf<VODescription>().apply {
            add(VOTitleSimple(localItemDescription.name))
            add(VOBodyLine(application.getString(R.string.cost, localItemDescription.cost)))
            add(VOBodyLine(application.getString(R.string.bought_from, localItemDescription.boughtFrom)))
            add(VOBodySimple(localItemDescription.description))

            localItemDescription.consistFrom?.let {
                add(VOTitleSimple(application.getString(R.string.recipe)))
                add(VOBodyRecipe(it.map { itemName -> itemName.toVORecipe() }))
            }

            localItemDescription.tips?.let {
                add(VOTitleSimple(application.getString(R.string.tips)))
                add(VOBodyWithSeparator(SpannableString(it.toStringListWithDash())))
            }

            localItemDescription.lore?.let {
                add(VOTitleSimple(application.getString(R.string.lore)))
                add(VOBodyWithSeparator(SpannableString(it.toStringListWithDash())))
            }

            localItemDescription.additionalInformation?.let {
                add(VOTitleSimple(application.getString(R.string.additional_information)))
                add(VOBodyWithSeparator(SpannableString(it.toStringListWithDash())))
            }

        }
    }
}