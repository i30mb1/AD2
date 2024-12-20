package n7.ad2.itempage.internal.domain.usecase

import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import n7.ad2.AppLocale
import n7.ad2.Resources
import n7.ad2.coroutines.DispatchersProvider
import n7.ad2.itempage.internal.domain.vo.VOItemInfo

class GetItemInfoUseCase @Inject constructor(
    private val res: Resources,
//    private val itemRepository: ItemRepository,
    private val dispatchers: DispatchersProvider,
) {

//    fun getItem(itemName: String, appLocale: AppLocale): String {
//        return res.getAssets("items/$itemName/${appLocale.value}/description.json")
//            .bufferedReader().use {
//                it.readText()
//            }
//    }

    operator fun invoke(itemName: String, appLocale: AppLocale): Flow<List<VOItemInfo>> = flow {
        emit(TODO())
//        val json = itemRepository.getItem(itemName, appLocale)
//        val localItemDescription = moshi.adapter(LocalItemInfo::class.java).fromJson(json)!!
//        val result = buildList {
//            if (localItemDescription.cost != null) add(VOItemInfo.TextLine(res.getString(R.string.cost, localItemDescription.cost)))
//            if (localItemDescription.boughtFrom != null) add(VOItemInfo.TextLine(res.getString(R.string.bought_from, localItemDescription.boughtFrom)))
//            if (localItemDescription.name != null) {
//                val voRecipes = localItemDescription.consistFrom?.map { itemName ->
//                    VORecipe(ItemRepository.getFullUrlItemImage(itemName), itemName)
//                } ?: emptyList()
//                add(VOItemInfo.Recipe(itemName, ItemRepository.getFullUrlItemImage(localItemDescription.name), voRecipes))
//            }
//            if (localItemDescription.description != null) add(VOItemInfo.Body(BodyViewHolder.Data(localItemDescription.description.toSpanned())))
//            if (localItemDescription.bonuses != null) add(VOItemInfo.Body(BodyViewHolder.Data(localItemDescription.bonuses.toStringList())))
//
//            localItemDescription.abilities?.let { list ->
//                for (ability in list) {
//                    if (ability.abilityName == null) continue
//                    add(VOItemInfo.Title(HeaderPlayableViewHolder.Data(res.getString(R.string.abilities, ability.abilityName), false, ability.audioUrl)))
//                    if (ability.effects != null) ability.effects.forEach { effect -> add(VOItemInfo.TextLine(effect)) }
//                    if (ability.description != null) add(VOItemInfo.Body(BodyViewHolder.Data(ability.description.toSpanned())))
//                    if (ability.story != null) add(VOItemInfo.Body(BodyViewHolder.Data(ability.story.toSpanned())))
//                    if (ability.params != null) add(VOItemInfo.Body(BodyViewHolder.Data(ability.params.toStringList(true))))
//                    if (ability.notes != null) add(VOItemInfo.Body(BodyViewHolder.Data(ability.notes.toStringList(true))))
//                    if (ability.mana != null) add(VOItemInfo.ImageLine(ImageLineViewHolder.Data(ability.mana.toSpanned(), n7.ad2.core.ui.R.drawable.mana)))
//                    if (ability.cooldown != null) add(VOItemInfo.ImageLine(ImageLineViewHolder.Data(ability.cooldown.toSpanned(), n7.ad2.core.ui.R.drawable.cooldown)))
//                }
//            }
//
//            localItemDescription.tips?.let { tips ->
//                add(VOItemInfo.Title(HeaderPlayableViewHolder.Data(res.getString(R.string.tips))))
//                add(VOItemInfo.Body(BodyViewHolder.Data(tips.toStringList(true))))
//            }
//
//            localItemDescription.lore?.let { lore ->
//                add(VOItemInfo.Title(HeaderPlayableViewHolder.Data(res.getString(R.string.lore))))
//                add(VOItemInfo.Body(BodyViewHolder.Data(lore.toStringList(true))))
//            }
//
//            localItemDescription.trivia?.let { trivia ->
//                add(VOItemInfo.Title(HeaderPlayableViewHolder.Data(res.getString(R.string.trivia))))
//                add(VOItemInfo.Body(BodyViewHolder.Data(trivia.toStringList(true))))
//            }
//
//            localItemDescription.additionalInformation?.let { additionalInfo ->
//                add(VOItemInfo.Title(HeaderPlayableViewHolder.Data(res.getString(R.string.additional_information))))
//                add(VOItemInfo.Body(BodyViewHolder.Data(additionalInfo.toStringList(true))))
//            }
//
//        }
//        emit(result)
    }.flowOn(dispatchers.IO)

}
