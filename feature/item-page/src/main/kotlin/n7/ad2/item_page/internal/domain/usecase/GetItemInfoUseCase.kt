package n7.ad2.item_page.internal.domain.usecase

import android.app.Application
import androidx.core.text.toSpanned
import com.squareup.moshi.Moshi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import n7.ad2.AppLocale
import n7.ad2.coroutines.DispatchersProvider
import n7.ad2.item_page.R
import n7.ad2.item_page.internal.domain.model.LocalItemInfo
import n7.ad2.item_page.internal.domain.vo.VOItemInfo
import n7.ad2.item_page.internal.domain.vo.VORecipe
import n7.ad2.ktx.toStringList
import n7.ad2.repositories.ItemRepository
import n7.ad2.ui.adapter.BodyViewHolder
import n7.ad2.ui.adapter.HeaderComplexViewHolder
import n7.ad2.ui.adapter.ImageLineViewHolder
import javax.inject.Inject

class GetItemInfoUseCase @Inject constructor(
    private val application: Application,
    private val itemRepository: ItemRepository,
    private val moshi: Moshi,
    private val dispatchers: DispatchersProvider,
) {

    @OptIn(ExperimentalStdlibApi::class)
    operator fun invoke(itemName: String, appLocale: AppLocale): Flow<List<VOItemInfo>> = flow {
        val json = itemRepository.getItem(itemName, appLocale)
        val localItemDescription = moshi.adapter(LocalItemInfo::class.java).fromJson(json)!!
        val result = buildList {
            add(VOItemInfo.TextLine(application.getString(R.string.cost, localItemDescription.cost)))
            add(VOItemInfo.TextLine(application.getString(R.string.bought_from, localItemDescription.boughtFrom)))
            add(VOItemInfo.Recipe(
                itemName,
                ItemRepository.getFullUrlItemImage(localItemDescription.name),
                localItemDescription.consistFrom?.map { itemName -> VORecipe(ItemRepository.getFullUrlItemImage(itemName), itemName) } ?: emptyList()
            ))
            add(VOItemInfo.Body(BodyViewHolder.Data(localItemDescription.description.toSpanned())))
            if (localItemDescription.bonuses != null) add(VOItemInfo.Body(BodyViewHolder.Data(localItemDescription.bonuses.toStringList().toSpanned())))

            localItemDescription.abilities?.let { list ->
                list.forEach { ability ->
                    add(VOItemInfo.Title(HeaderComplexViewHolder.Data(application.getString(R.string.abilities, ability.abilityName), false, ability.audioUrl)))
                    ability.effects.forEach { effect -> add(VOItemInfo.TextLine(effect)) }
                    add(VOItemInfo.Body(BodyViewHolder.Data(ability.description.toSpanned())))
                    if (ability.story != null) add(VOItemInfo.Body(BodyViewHolder.Data(ability.story.toSpanned())))
                    add(VOItemInfo.Body(BodyViewHolder.Data(ability.params.toStringList(true).toSpanned())))
                    add(VOItemInfo.Body(BodyViewHolder.Data(ability.notes.toStringList(true).toSpanned())))
                    if (ability.mana != null) add(VOItemInfo.ImageLine(ImageLineViewHolder.Data(ability.mana.toSpanned(), n7.ad2.ui.R.drawable.mana)))
                    if (ability.cooldown != null) add(VOItemInfo.ImageLine(ImageLineViewHolder.Data(ability.cooldown.toSpanned(), n7.ad2.ui.R.drawable.cooldown)))
                }
            }

            localItemDescription.tips?.let { tips ->
                add(VOItemInfo.Title(HeaderComplexViewHolder.Data(application.getString(R.string.tips))))
                add(VOItemInfo.Body(BodyViewHolder.Data(tips.toStringList(true).toSpanned())))
            }

            localItemDescription.lore?.let { lore ->
                add(VOItemInfo.Title(HeaderComplexViewHolder.Data(application.getString(R.string.lore))))
                add(VOItemInfo.Body(BodyViewHolder.Data(lore.toStringList(true).toSpanned())))
            }

            localItemDescription.trivia?.let { trivia ->
                add(VOItemInfo.Title(HeaderComplexViewHolder.Data(application.getString(R.string.trivia))))
                add(VOItemInfo.Body(BodyViewHolder.Data(trivia.toStringList(true).toSpanned())))
            }

            localItemDescription.additionalInformation?.let { additionalInfo ->
                add(VOItemInfo.Title(HeaderComplexViewHolder.Data(application.getString(R.string.additional_information))))
                add(VOItemInfo.Body(BodyViewHolder.Data(additionalInfo.toStringList(true).toSpanned())))
            }

        }
        emit(result)
    }.flowOn(dispatchers.IO)

}