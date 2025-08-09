package n7.ad2.heroes.domain.internal.usecase

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.serialization.json.Json
import n7.ad2.heroes.domain.internal.HeroesRepository
import n7.ad2.heroes.domain.model.Guide
import n7.ad2.heroes.domain.usecase.GetGuideForHeroUseCase

internal class GetGuideForHeroUseCaseImpl(
    private val heroesRepository: HeroesRepository,
) : GetGuideForHeroUseCase {

    private val json = Json { ignoreUnknownKeys = true }

    override fun invoke(name: String): Flow<List<Guide>> {
        return emptyFlow()
//        return heroesRepository.getHeroWithGuides(name)
//            .map { heroWithGuides ->
//                heroWithGuides.guides
//                    .map {
//                        json.decodeFromString<LocalGuideJson>(it.json)
//                    }
//                    .map { localGuide ->
//                        Guide(
//                            localGuide.heroName,
//                            localGuide.heroWinrate,
//                            localGuide.heroPopularity,
//                            localGuide.detailedGuide.guideTime,
//                            localGuide.hardToWinHeroList.map { HeroWithWinrate(it.heroName, it.heroWinrate, "file:///android_asset/heroes/${it.heroName}/full.png") },
//                            localGuide.easyToWinHeroList.map { HeroWithWinrate(it.heroName, it.heroWinrate, "file:///android_asset/heroes/${it.heroName}/full.png") },
//                            localGuide.detailedGuide.heroStartingHeroItemsList.map { HeroItem(it.itemName, it.itemTime) },
//                            localGuide.detailedGuide.heroItemsList.map { HeroItem(it.itemName, it.itemTime) },
//                            localGuide.detailedGuide.heroSpellsList.map { Spell(it.spellName, it.spellOrder, getFullUrlHeroSpell(it.spellName)) },
//                        )
//                    }
//            }
    }
}
