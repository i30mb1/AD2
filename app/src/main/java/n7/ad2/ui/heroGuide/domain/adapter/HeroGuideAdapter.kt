package n7.ad2.ui.heroGuide.domain.adapter

import n7.ad2.data.source.local.Repository
import n7.ad2.ui.heroGuide.VOHeroFlowItem
import n7.ad2.ui.heroGuide.domain.model.HeroWithWinrate
import n7.ad2.ui.heroGuide.domain.vo.VOEasyToWinHeroes
import n7.ad2.ui.heroGuide.domain.vo.VOHardToWinHeroes

fun List<HeroWithWinrate>.toVOHardToWinHeroes(): VOHardToWinHeroes = VOHardToWinHeroes(
    map {
        VOHeroFlowItem(
            it.heroName,
            Repository.getFullUrlHeroImage(it.heroName),
            "${it.heroWinrate}%")
    }
)

fun List<HeroWithWinrate>.toVOEasyToWinHeroes(): VOEasyToWinHeroes = VOEasyToWinHeroes(
    map {
        VOHeroFlowItem(it.heroName,
            Repository.getFullUrlHeroImage(it.heroName),
            "${it.heroWinrate}%")
    }
)
