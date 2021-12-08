package n7.ad2.item_page.internal.domain.usecase

import android.app.Application
import kotlinx.coroutines.withContext
import n7.ad2.coroutines.DispatchersProvider
import n7.ad2.item_page.internal.domain.model.LocalItemInfo
import n7.ad2.item_page.internal.domain.vo.ItemInfo
import javax.inject.Inject

class GetVOItemInfoUseCase @Inject constructor(
    private val dispatchers: DispatchersProvider,
    private val application: Application,
) {

    @OptIn(ExperimentalStdlibApi::class)
    suspend operator fun invoke(localItemDescription: LocalItemInfo): List<ItemInfo> = withContext(dispatchers.IO) {
        buildList {
//            add(VOItemInfoLine(application.getString(R.string.cost, localItemDescription.cost)))
//            add(VOItemInfoLine(application.getString(R.string.bought_from, localItemDescription.boughtFrom)))
//            add(VOItemInfoRecipe(ItemRepository.getFullUrlItemImage(localItemDescription.name), localItemDescription.consistFrom?.map { itemName -> itemName.toVORecipe() } ?: emptyList()))
//            add(VOItemInfoBody(SpannableString(localItemDescription.description)))
//            if (localItemDescription.bonuses != null) add(VOItemInfoBody(SpannableString(localItemDescription.bonuses.toStringList())))

            localItemDescription.abilities?.let { list ->
                list.forEach { ability ->
//                    add(VOItemInfoTitle(application.getString(R.string.abilities, ability.abilityName), ability.audioUrl!!))
//                    ability.effects.forEach { add(VOItemInfoLine(it)) }
//                    add(VOItemInfoBody(SpannableString(ability.description)))
//                    if (ability.story != null) add(VOItemInfoBody(SpannableString(ability.story)))
//                    add(VOItemInfoBody(SpannableString(ability.params.toStringListWithDash())))
//                    add(VOItemInfoBody(SpannableString(ability.notes.toStringListWithDash())))
//                    if (ability.mana != null) add(VOItemInfoLineImage(SpannableString(ability.mana), R.drawable.mana))
//                    if (ability.cooldown != null) add(VOItemInfoLineImage(SpannableString(ability.cooldown), R.drawable.cooldown))
                }
            }

            localItemDescription.tips?.let {
//                add(VOItemInfoTitle(application.getString(R.string.tips)))
//                add(VOItemInfoBody(SpannableString(it.toStringListWithDash())))
            }

            localItemDescription.lore?.let {
//                add(VOItemInfoTitle(application.getString(R.string.lore)))
//                add(VOItemInfoBody(SpannableString(it.toStringListWithDash())))
            }

            localItemDescription.trivia?.let {
//                add(VOItemInfoTitle(application.getString(R.string.trivia)))
//                add(VOItemInfoBody(SpannableString(it.toStringListWithDash())))
            }

            localItemDescription.additionalInformation?.let {
//                add(VOItemInfoTitle(application.getString(R.string.additional_information)))
//                add(VOItemInfoBody(SpannableString(it.toStringListWithDash())))
            }

        }
    }

}