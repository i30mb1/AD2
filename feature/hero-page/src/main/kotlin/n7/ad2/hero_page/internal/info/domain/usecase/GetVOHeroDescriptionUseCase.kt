package n7.ad2.hero_page.internal.info.domain.usecase

import androidx.core.text.toSpanned
import com.squareup.moshi.Moshi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import n7.ad2.AppLocale
import n7.ad2.AppResources
import n7.ad2.coroutines.DispatchersProvider
import n7.ad2.hero_page.R
import n7.ad2.hero_page.internal.info.HeroStatistics
import n7.ad2.hero_page.internal.info.domain.model.LocalHeroDescription
import n7.ad2.hero_page.internal.info.domain.vo.VOHeroInfo
import n7.ad2.hero_page.internal.info.domain.vo.VOSpell
import n7.ad2.ktx.toStringList
import n7.ad2.logger.AD2Logger
import n7.ad2.repositories.HeroRepository
import n7.ad2.span_parser.AD2StringParser
import n7.ad2.ui.adapter.BodyViewHolder
import n7.ad2.ui.adapter.HeaderViewHolder
import javax.inject.Inject

class GetVOHeroDescriptionUseCase @Inject constructor(
    private val res: AppResources,
    private val heroRepository: HeroRepository,
    private val aD2StringParser: AD2StringParser,
    private val moshi: Moshi,
    private val logger: AD2Logger,
    private val dispatchers: DispatchersProvider,
) {

    sealed class HeroInfo {
        object Main : HeroInfo()
        data class Spell(val name: String) : HeroInfo()
    }

    @OptIn(ExperimentalStdlibApi::class)
    suspend operator fun invoke(heroName: String, locale: AppLocale, heroInfo: HeroInfo? = null): Flow<List<VOHeroInfo>> = flow {
        val localHero = heroRepository.getHero(heroName)
        val json = heroRepository.getHeroDescription(localHero.name, locale)
        val info = moshi.adapter(LocalHeroDescription::class.java).fromJson(json)!!

        emit(buildList {
            add(VOHeroInfo.Attributes(
                HeroRepository.getFullUrlHeroImage(localHero.name),
                HeroStatistics.Statistics(
                    info.mainAttributes.attrStrength,
                    info.mainAttributes.attrAgility,
                    info.mainAttributes.attrIntelligence
                ),
                heroInfo == HeroInfo.Main
            ))
            add(VOHeroInfo.Spells(
                info.abilities.map { ability ->
                    val name = ability.name
                    val urlSpellImage = HeroRepository.getFullUrlHeroSpell(ability.name)
                    val isSelected = (heroInfo as? HeroInfo.Spell)?.name == ability.name
                    VOSpell(name, urlSpellImage, isSelected)
                }
            ))
            when (heroInfo) {
                HeroInfo.Main -> {
                    add(VOHeroInfo.Header(HeaderViewHolder.Data(res.getString(R.string.hero_fragment_description))))
                    add(VOHeroInfo.Body(BodyViewHolder.Data(info.description.toSpanned())))
                    add(VOHeroInfo.Body(BodyViewHolder.Data(
                        aD2StringParser.toSpannable("Этот спелл <span image=\"Starstorm.webp\">Starstorm</span> пиздец 111111111111111111111111111111111111111111111111111111111111111111111111111111")
                    )))
                    add(VOHeroInfo.Header(HeaderViewHolder.Data(res.getString(R.string.hero_fragment_bio))))
                    add(VOHeroInfo.Body(BodyViewHolder.Data(info.history.toSpanned())))
                    info.trivia?.let { list ->
                        add(VOHeroInfo.Header(HeaderViewHolder.Data(res.getString(R.string.hero_fragment_trivia))))
                        add(VOHeroInfo.Body(BodyViewHolder.Data(list.toStringList(true).toSpanned())))
                    }
                }
                is HeroInfo.Spell -> {
                    val selectedSpell = info.abilities.find { ability -> ability.name == heroInfo.name } ?: return@buildList
//                    add(VOHeroInfo.HeaderSound(selectedSpell.name, selectedSpell.hotKey, selectedSpell.legacyKey, selectedSpell.audioUrl, false))
                }
            }
        })
    }.flowOn(dispatchers.IO)

}