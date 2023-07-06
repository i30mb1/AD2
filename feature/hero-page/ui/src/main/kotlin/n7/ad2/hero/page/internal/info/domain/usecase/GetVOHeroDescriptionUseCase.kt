package n7.ad2.hero.page.internal.info.domain.usecase

import androidx.core.text.toSpanned
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.single
import n7.ad2.AppInformation
import n7.ad2.AppLocale
import n7.ad2.Resources
import n7.ad2.app.logger.Logger
import n7.ad2.coroutines.DispatchersProvider
import n7.ad2.feature.heropage.ui.R
import n7.ad2.hero.page.internal.info.HeroStatistics
import n7.ad2.hero.page.internal.info.domain.vo.VOHeroInfo
import n7.ad2.hero.page.internal.info.domain.vo.VOSpell
import n7.ad2.heroes.domain.GetHeroByNameUseCase
import n7.ad2.heroes.domain.Hero
import n7.ad2.ktx.toStringList
import n7.ad2.repositories.HeroRepository
import n7.ad2.repositories.model.LocalHeroDescription
import n7.ad2.spanparser.SpanParser
import n7.ad2.ui.adapter.BodyViewHolder
import n7.ad2.ui.adapter.HeaderViewHolder
import javax.inject.Inject

class GetVOHeroDescriptionUseCase @Inject constructor(
    private val res: Resources,
    private val heroRepository: HeroRepository,
    private val getHeroByNameUseCase: GetHeroByNameUseCase,
    private val spanParser: SpanParser,
    private val logger: Logger,
    private val dispatchers: DispatchersProvider,
    private val appInformation: AppInformation,
) {

    sealed class HeroInfo {
        object Main : HeroInfo()
        data class Spell(val name: String) : HeroInfo()
    }

    suspend operator fun invoke(
        heroName: String,
        locale: AppLocale,
        heroInfo: HeroInfo,
    ): Flow<List<VOHeroInfo>> = flow {
        val localHero = getHeroByNameUseCase(heroName)
        val info = heroRepository.getHeroDescription(localHero.name, locale).single()
        emit(buildList {
            add(getAttributes(localHero, info, heroInfo))
            add(getSpells(info, heroInfo))
            when (heroInfo) {
                HeroInfo.Main -> {
                    add(VOHeroInfo.Header(HeaderViewHolder.Data(res.getString(R.string.hero_fragment_description))))
                    add(VOHeroInfo.Body(BodyViewHolder.Data(info.description.toSpanned())))
                    add(VOHeroInfo.Header(HeaderViewHolder.Data(res.getString(R.string.hero_fragment_bio))))
                    add(VOHeroInfo.Body(BodyViewHolder.Data(info.history.toSpanned())))
                    info.trivia?.let { list ->
                        add(VOHeroInfo.Header(HeaderViewHolder.Data(res.getString(R.string.hero_fragment_trivia))))
                        add(VOHeroInfo.Body(BodyViewHolder.Data(list.toStringList(true))))
                    }
                }

                is HeroInfo.Spell -> {
                    val selectedSpell = info.abilities.find { ability -> ability.name == heroInfo.name } ?: error("could not find ability")
                    add(VOHeroInfo.HeaderSound(selectedSpell.name, selectedSpell.hotKey, selectedSpell.legacyKey, false, selectedSpell.audioUrl))
                    selectedSpell.description?.let { add(VOHeroInfo.Body(BodyViewHolder.Data(it.toSpanned()))) }
                    selectedSpell.story?.let { add(VOHeroInfo.Body(BodyViewHolder.Data(it.toSpanned()))) }
                    selectedSpell.notes?.let { notes ->
                        add(VOHeroInfo.Header(HeaderViewHolder.Data(res.getString(R.string.hero_fragment_notes))))
                        val text = notes
                            .mapNotNull { spanParser.toSpannable(it, appInformation.isNightMode) }
                            .toStringList(true)
                        add(VOHeroInfo.Body(BodyViewHolder.Data(text)))
                    }
                    selectedSpell.params?.let { params ->
                        add(VOHeroInfo.Header(HeaderViewHolder.Data(res.getString(R.string.hero_fragment_params))))
                        val text = params
                            .mapNotNull { spanParser.toSpannable(it, appInformation.isNightMode) }
                            .toStringList(true)
                        add(VOHeroInfo.Body(BodyViewHolder.Data(text)))
                    }
                }
            }
        })
    }
        .onStart { logger.log("parse hero info") }
        .flowOn(dispatchers.IO)

    private fun getAttributes(hero: Hero, info: LocalHeroDescription, heroInfo: HeroInfo): VOHeroInfo.Attributes {
        return VOHeroInfo.Attributes(
            hero.avatarUrl,
            HeroStatistics.Statistics(
                info.mainAttributes.attrStrength,
                info.mainAttributes.attrAgility,
                info.mainAttributes.attrIntelligence
            ),
            heroInfo == HeroInfo.Main,
        )
    }

    private fun getSpells(info: LocalHeroDescription, heroInfo: HeroInfo): VOHeroInfo.Spells {
        val spells = info.abilities.map { ability ->
            val name = ability.name
            val isSelected = (heroInfo as? HeroInfo.Spell)?.name == ability.name
            val urlSpellImage = HeroRepository.getFullUrlHeroSpell(ability.name)
            when (name) {
                "Talent" -> VOSpell.Talent(name, isSelected)
                else -> VOSpell.Simple(name, urlSpellImage, isSelected)
            }
        }
        return VOHeroInfo.Spells(spells)
    }

}