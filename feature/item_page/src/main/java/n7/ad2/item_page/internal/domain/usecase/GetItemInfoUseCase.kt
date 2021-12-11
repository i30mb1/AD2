package n7.ad2.item_page.internal.domain.usecase

import android.app.Application
import android.text.SpannedString
import com.squareup.moshi.Moshi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import n7.ad2.android.Locale
import n7.ad2.coroutines.DispatchersProvider
import n7.ad2.item_page.R
import n7.ad2.item_page.internal.domain.model.LocalItemInfo
import n7.ad2.item_page.internal.domain.vo.VOItemInfo
import n7.ad2.item_page.internal.domain.vo.VORecipe
import n7.ad2.repositories.ItemRepository
import n7.ad2.ui.adapter.BodyViewHolder
import javax.inject.Inject

class GetItemInfoUseCase @Inject constructor(
    private val application: Application,
    private val itemRepository: ItemRepository,
    private val moshi: Moshi,
    private val dispatchers: DispatchersProvider,
) {

    @OptIn(ExperimentalStdlibApi::class)
    operator fun invoke(itemName: String, locale: Locale): Flow<List<VOItemInfo>> = flow {
        val json = itemRepository.getItem(itemName, locale)
        val localItemDescription = moshi.adapter(LocalItemInfo::class.java).fromJson(json)!!
        val result = buildList {
            add(VOItemInfo.TextLine(application.getString(R.string.cost, localItemDescription.cost)))
            add(VOItemInfo.TextLine(application.getString(R.string.bought_from, localItemDescription.boughtFrom)))
            add(VOItemInfo.Recipe(
                ItemRepository.getFullUrlItemImage(localItemDescription.name),
                localItemDescription.consistFrom?.map { itemName -> VORecipe(ItemRepository.getFullUrlItemImage(itemName), itemName) } ?: emptyList()
            ))
            add(VOItemInfo.Body(BodyViewHolder.Data(SpannedString(localItemDescription.description))))
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
        emit(result)
    }.flowOn(dispatchers.IO)

}