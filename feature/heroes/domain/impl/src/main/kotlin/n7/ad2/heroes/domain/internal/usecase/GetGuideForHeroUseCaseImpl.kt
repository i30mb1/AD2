package n7.ad2.heroes.domain.internal.usecase

import com.squareup.moshi.Moshi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import n7.ad2.heroes.domain.internal.HeroesRepository
import n7.ad2.heroes.domain.internal.data.model.LocalGuideJson
import n7.ad2.heroes.domain.internal.getFullUrlHeroImage
import n7.ad2.heroes.domain.internal.getFullUrlHeroSpell
import n7.ad2.heroes.domain.model.Guide
import n7.ad2.heroes.domain.model.HeroItem
import n7.ad2.heroes.domain.model.HeroWithWinrate
import n7.ad2.heroes.domain.model.Spell
import n7.ad2.heroes.domain.usecase.GetGuideForHeroUseCase

internal class GetGuideForHeroUseCaseImpl(
    private val heroesRepository: HeroesRepository,
    private val moshi: Moshi,
) : GetGuideForHeroUseCase {

    override fun invoke(name: String): Flow<List<Guide>> {
        return heroesRepository.getHeroWithGuides(name)
            .map { heroWithGuides ->
                heroWithGuides.guides
                    .map {
                        moshi.adapter(LocalGuideJson::class.java).fromJson(it.json)!!
                    }
                    .map { localGuide ->
                        Guide(
                            localGuide.heroName,
                            localGuide.heroWinrate,
                            localGuide.heroPopularity,
                            localGuide.detailedGuide.guideTime,
                            localGuide.hardToWinHeroList.map { HeroWithWinrate(it.heroName, it.heroWinrate, getFullUrlHeroImage(it.heroName)) },
                            localGuide.easyToWinHeroList.map { HeroWithWinrate(it.heroName, it.heroWinrate, getFullUrlHeroImage(it.heroName)) },
                            localGuide.detailedGuide.heroStartingHeroItemsList.map { HeroItem(it.itemName, it.itemTime) },
                            localGuide.detailedGuide.heroItemsList.map { HeroItem(it.itemName, it.itemTime) },
                            localGuide.detailedGuide.heroSpellsList.map { Spell(it.spellName, it.spellOrder, getFullUrlHeroSpell(it.spellName)) },
                        )
                    }
            }
    }
}
