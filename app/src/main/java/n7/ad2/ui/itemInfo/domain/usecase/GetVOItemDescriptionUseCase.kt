package n7.ad2.ui.itemInfo.domain.usecase

import android.app.Application
import android.text.SpannableString
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import n7.ad2.R
import n7.ad2.data.source.local.ItemRepository
import n7.ad2.ui.heroInfo.domain.vo.VOBodyLine
import n7.ad2.ui.heroInfo.domain.vo.VOBodyRecipe
import n7.ad2.ui.heroInfo.domain.vo.VOBodySimple
import n7.ad2.ui.heroInfo.domain.vo.VOBodyWithImage
import n7.ad2.ui.heroInfo.domain.vo.VOBodyWithSeparator
import n7.ad2.ui.heroInfo.domain.vo.VODescription
import n7.ad2.ui.heroInfo.domain.vo.VOTitle
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
            add(VOTitle(localItemDescription.name))
            add(VOBodyLine(application.getString(R.string.cost, localItemDescription.cost)))
            add(VOBodyLine(application.getString(R.string.bought_from, localItemDescription.boughtFrom)))
            add(VOBodyRecipe(ItemRepository.getFullUrlItemImage(localItemDescription.name) ,localItemDescription.consistFrom?.map { itemName -> itemName.toVORecipe() } ?: emptyList()))
            add(VOBodySimple(localItemDescription.description))
            if(localItemDescription.bonuses != null) add(VOBodyWithSeparator(SpannableString(localItemDescription.bonuses.toStringListWithDash())))


            localItemDescription.abilities?.let { list ->
                list.forEach { ability ->
                    add(VOTitle(application.getString(R.string.abilities, ability.abilityName), null, null, ability.audioUrl))
                    ability.effects.forEach { add(VOBodyLine(it)) }
                    add(VOBodySimple(ability.description))
                    if (ability.story != null) add(VOBodySimple(ability.story))
                    add(VOBodyWithSeparator(SpannableString(ability.params.toStringListWithDash())))
                    add(VOBodyWithSeparator(SpannableString(ability.notes.toStringListWithDash())))
                    if (ability.mana != null) add(VOBodyWithImage(SpannableString(ability.mana), R.drawable.mana))
                    if (ability.cooldown != null) add(VOBodyWithImage(SpannableString(ability.cooldown), R.drawable.cooldown))
                }
            }

            localItemDescription.tips?.let {
                add(VOTitle(application.getString(R.string.tips)))
                add(VOBodyWithSeparator(SpannableString(it.toStringListWithDash())))
            }

            localItemDescription.lore?.let {
                add(VOTitle(application.getString(R.string.lore)))
                add(VOBodyWithSeparator(SpannableString(it.toStringListWithDash())))
            }

            localItemDescription.trivia?.let {
                add(VOTitle(application.getString(R.string.trivia)))
                add(VOBodyWithSeparator(SpannableString(it.toStringListWithDash())))
            }

            localItemDescription.additionalInformation?.let {
                add(VOTitle(application.getString(R.string.additional_information)))
                add(VOBodyWithSeparator(SpannableString(it.toStringListWithDash())))
            }

        }
    }
}