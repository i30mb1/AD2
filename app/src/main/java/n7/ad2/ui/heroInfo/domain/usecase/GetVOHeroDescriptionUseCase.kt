package n7.ad2.ui.heroInfo.domain.usecase

import android.app.Application
import androidx.core.text.toSpanned
import com.squareup.moshi.Moshi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import n7.ad2.AD2Logger
import n7.ad2.AD2StringParser
import n7.ad2.R
import n7.ad2.base.DispatchersProvider
import n7.ad2.base.adapter.BodyViewHolder
import n7.ad2.base.adapter.HeaderViewHolder
import n7.ad2.data.source.local.HeroRepository
import n7.ad2.data.source.local.Locale
import n7.ad2.ui.heroInfo.HeroStatistics
import n7.ad2.ui.heroInfo.domain.model.LocalHeroDescription
import n7.ad2.ui.heroInfo.domain.vo.VOHeroInfo
import n7.ad2.ui.heroInfo.domain.vo.VOSpell
import n7.ad2.utils.extension.toTextWithDash
import javax.inject.Inject

class GetVOHeroDescriptionUseCase @Inject constructor(
    private val application: Application,
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
    suspend operator fun invoke(heroName: String, locale: Locale, heroInfo: HeroInfo? = null): Flow<List<VOHeroInfo>> = flow {
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
                    add(VOHeroInfo.Header(HeaderViewHolder.Data(application.getString(R.string.hero_fragment_description))))
                    add(VOHeroInfo.Body(BodyViewHolder.Data(info.description.toSpanned())))
                    add(VOHeroInfo.Body(BodyViewHolder.Data(
                        aD2StringParser.toSpannable("Этот спелл <span image=\"Starstorm.webp\">Starstorm</span> пиздец 111111111111111111111111111111111111111111111111111111111111111111111111111111")
                    )))
                    add(VOHeroInfo.Header(HeaderViewHolder.Data(application.getString(R.string.hero_fragment_bio))))
                    add(VOHeroInfo.Body(BodyViewHolder.Data(info.history.toSpanned())))
                    info.trivia?.let { list ->
                        add(VOHeroInfo.Header(HeaderViewHolder.Data(application.getString(R.string.hero_fragment_trivia))))
                        add(VOHeroInfo.Body(BodyViewHolder.Data(list.toTextWithDash())))
                    }
                }
                is HeroInfo.Spell -> {
                    val selectedSpell = info.abilities.find { ability -> ability.name == heroInfo.name } ?: return@buildList
                    add(VOHeroInfo.HeaderSound(selectedSpell.name, selectedSpell.hotKey, selectedSpell.legacyKey, selectedSpell.audioUrl, false))
                }
            }
        })
    }.flowOn(dispatchers.IO)

}