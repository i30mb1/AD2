package n7.ad2.ui.heroInfo.domain.usecase

import android.app.Application
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import n7.ad2.data.source.local.model.LocalHero
import n7.ad2.ui.heroInfo.domain.model.LocalHeroDescription
import n7.ad2.ui.heroInfo.domain.vo.VOHeroInfo
import javax.inject.Inject

class GetVOHeroDescriptionUseCase @Inject constructor(
    private val ioDispatcher: CoroutineDispatcher,
    private val application: Application,
) {

    @OptIn(ExperimentalStdlibApi::class)
    suspend operator fun invoke(localHeroDescription: LocalHeroDescription, localHero: LocalHero): List<VOHeroInfo> = withContext(ioDispatcher) {
        buildList {
//            add(VOHeroInfoHeaderSound("spellName", "hotKey", "legacyKey", "audioUrl!!"))
//            val voHeroAttrs = localHeroDescription.toVOHeroAttrs(application, localHero.name)
//            add(voHeroAttrs)

//            val voHeroSpells = localHeroDescription.toVOHeroSpells(application)
//            add(voHeroSpells)
//            addAll(voHeroAttrs.voDescriptionList)
        }
    }
}