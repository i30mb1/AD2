package n7.ad2.ui.heroInfo.domain.usecase

import android.app.Application
import android.text.style.ClickableSpan
import android.view.View
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import n7.ad2.base.VOPopUpListener
import n7.ad2.data.source.local.model.LocalHero
import n7.ad2.ui.heroInfo.domain.adapter.toVOHeroAttrs
import n7.ad2.ui.heroInfo.domain.adapter.toVOSpell
import n7.ad2.ui.heroInfo.domain.model.LocalHeroDescription
import n7.ad2.ui.heroInfo.domain.vo.VODescription
import n7.ad2.ui.heroInfo.domain.vo.VOHeroSpells
import n7.ad2.ui.heroInfo.domain.vo.VOSpell
import javax.inject.Inject

class GetVOHeroDescriptionUseCase @Inject constructor(
    private val ioDispatcher: CoroutineDispatcher,
    private val application: Application,
) {

    @ExperimentalStdlibApi
    suspend operator fun invoke(localHeroDescription: LocalHeroDescription, localHero: LocalHero): List<VODescription> = withContext(ioDispatcher) {
        buildList {
            val voHeroAttrs = localHeroDescription.mainAttributes.toVOHeroAttrs(localHero.name)
            add(voHeroAttrs)

            val spells: List<VOSpell> = localHeroDescription.abilities.map { it.toVOSpell(application) }
            add(VOHeroSpells(spells))
        }
//
//        val heroBio = mutableListOf<VODescription>().apply {
//            add(VOTitle(application.getString(R.string.hero_fragment_description)))
//            add(VOBodyWithSeparator(SpannableString(localHeroDescription.description)))
//
//            add(VOTitle(application.getString(R.string.hero_fragment_bio)))
//            add(VOBodyWithSeparator(SpannableString(localHeroDescription.history)))
//
//            add(VOTitle(application.getString(R.string.hero_fragment_trivia)))
//            add(VOBodyWithSeparator(SpannableString(localHeroDescription.trivia.toStringListWithDash())))
//        }
//        voHeroDescription.heroBio = heroBio
//        voHeroDescription.selectedDescriptionList = heroBio
//
//        voHeroDescription
    }

}