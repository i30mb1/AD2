package n7.ad2.ui.heroInfo.domain.usecase

import android.app.Application
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import n7.ad2.base.DispatchersProvider
import n7.ad2.data.source.local.HeroRepository
import n7.ad2.data.source.local.model.LocalHero
import n7.ad2.ui.heroInfo.HeroStatistics
import n7.ad2.ui.heroInfo.domain.model.LocalHeroDescription
import n7.ad2.ui.heroInfo.domain.vo.VOHeroInfo
import n7.ad2.ui.heroInfo.domain.vo.VOHeroInfoMain
import javax.inject.Inject

class GetVOHeroDescriptionUseCase @Inject constructor(
    private val application: Application,
    private val dispatchers: DispatchersProvider,
) {

    @OptIn(ExperimentalStdlibApi::class)
    suspend operator fun invoke(
        localHeroDescription: LocalHeroDescription,
        localHero: LocalHero,
    ): Flow<List<VOHeroInfo>> = flow {
        emit(buildList {
            add(VOHeroInfoMain(
                HeroRepository.getFullUrlHeroImage(localHero.name),
                HeroStatistics.Companion.Statistics(
                    localHeroDescription.mainAttributes.attrStrength,
                    localHeroDescription.mainAttributes.attrAgility,
                    localHeroDescription.mainAttributes.attrIntelligence
                )
            ))
        })
//            add(VOHeroInfoHeaderSound("spellName", "hotKey", "legacyKey", "audioUrl!!"))
//            val voHeroAttrs = localHeroDescription.toVOHeroAttrs(application, localHero.name)
//            add(voHeroAttrs)

//            val voHeroSpells = localHeroDescription.toVOHeroSpells(application)
//            add(voHeroSpells)
//            addAll(voHeroAttrs.voDescriptionList)
//        }
    }.flowOn(dispatchers.IO)
}