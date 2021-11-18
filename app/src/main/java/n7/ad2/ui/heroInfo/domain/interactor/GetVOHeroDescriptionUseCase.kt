package n7.ad2.ui.heroInfo.domain.interactor

import android.app.Application
import androidx.core.text.toSpanned
import com.squareup.moshi.Moshi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onStart
import n7.ad2.AD2Logger
import n7.ad2.R
import n7.ad2.base.DispatchersProvider
import n7.ad2.base.adapter.BodyViewHolder
import n7.ad2.base.adapter.HeaderViewHolder
import n7.ad2.data.source.local.HeroRepository
import n7.ad2.data.source.local.Locale
import n7.ad2.data.source.local.model.LocalHero
import n7.ad2.ui.heroInfo.HeroStatistics
import n7.ad2.ui.heroInfo.domain.model.LocalHeroDescription
import n7.ad2.ui.heroInfo.domain.vo.VOHeroInfo
import n7.ad2.ui.heroInfo.domain.vo.VOHeroInfoBody
import n7.ad2.ui.heroInfo.domain.vo.VOHeroInfoHeader
import n7.ad2.ui.heroInfo.domain.vo.VOHeroInfoMain
import n7.ad2.utils.extension.toTextWithDash
import javax.inject.Inject

class GetVOHeroDescriptionUseCase @Inject constructor(
    private val application: Application,
    private val heroRepository: HeroRepository,
    private val moshi: Moshi,
    private val logger: AD2Logger,
    private val dispatchers: DispatchersProvider,
) {

    @OptIn(ExperimentalStdlibApi::class)
    suspend operator fun invoke(localHero: LocalHero, locale: Locale): Flow<List<VOHeroInfo>> = flow {
        val json = heroRepository.getHeroDescription(localHero.name, locale)
        val info = moshi.adapter(LocalHeroDescription::class.java).fromJson(json)!!

        emit(buildList {
            add(VOHeroInfoMain(
                HeroRepository.getFullUrlHeroImage(localHero.name),
                HeroStatistics.Companion.Statistics(
                    info.mainAttributes.attrStrength,
                    info.mainAttributes.attrAgility,
                    info.mainAttributes.attrIntelligence
                )
            ))
            add(VOHeroInfoHeader(HeaderViewHolder.Data(application.getString(R.string.hero_fragment_description))))
            add(VOHeroInfoBody(BodyViewHolder.Data(info.description.toSpanned())))
            add(VOHeroInfoHeader(HeaderViewHolder.Data(application.getString(R.string.hero_fragment_bio))))
            add(VOHeroInfoBody(BodyViewHolder.Data(info.history.toSpanned())))
            info.trivia?.let { list ->
                add(VOHeroInfoHeader(HeaderViewHolder.Data(application.getString(R.string.hero_fragment_trivia))))
                add(VOHeroInfoBody(BodyViewHolder.Data(list.toTextWithDash())))
            }
        })
    }.onStart { logger.log("load ${localHero.name} description") }
        .flowOn(dispatchers.IO)

}