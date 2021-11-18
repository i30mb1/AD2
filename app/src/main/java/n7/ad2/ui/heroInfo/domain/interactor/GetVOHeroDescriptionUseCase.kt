package n7.ad2.ui.heroInfo.domain.interactor

import com.squareup.moshi.Moshi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onStart
import n7.ad2.AD2Logger
import n7.ad2.base.DispatchersProvider
import n7.ad2.data.source.local.HeroRepository
import n7.ad2.data.source.local.Locale
import n7.ad2.data.source.local.model.LocalHero
import n7.ad2.ui.heroInfo.HeroStatistics
import n7.ad2.ui.heroInfo.domain.model.LocalHeroDescription
import n7.ad2.ui.heroInfo.domain.vo.VOHeroInfo
import n7.ad2.ui.heroInfo.domain.vo.VOHeroInfoMain
import javax.inject.Inject

class GetVOHeroDescriptionUseCase @Inject constructor(
    private val heroRepository: HeroRepository,
    private val moshi: Moshi,
    private val logger: AD2Logger,
    private val dispatchers: DispatchersProvider,
) {

    @OptIn(ExperimentalStdlibApi::class)
    suspend operator fun invoke(localHero: LocalHero, locale: Locale): Flow<List<VOHeroInfo>> = flow {
        val json = heroRepository.getHeroDescription(localHero.name, locale)
        val localHeroDescription = moshi.adapter(LocalHeroDescription::class.java).fromJson(json)!!

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
    }.onStart { logger.log("load ${localHero.name} description") }
        .flowOn(dispatchers.IO)

}