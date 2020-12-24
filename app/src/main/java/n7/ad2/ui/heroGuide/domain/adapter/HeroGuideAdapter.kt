package n7.ad2.ui.heroGuide.domain.adapter

import n7.ad2.data.source.local.Repository
import n7.ad2.ui.heroGuide.VOHeroFlowItem
import n7.ad2.ui.heroGuide.domain.vo.VOEasyToWinHeroes
import n7.ad2.ui.heroGuide.domain.vo.VOHardToWinHeroes

fun List<String>.toVOHardToWinHeroes(): VOHardToWinHeroes = VOHardToWinHeroes(
    map { VOHeroFlowItem(it, Repository.getFullUrlHeroImage(it)) }
)

fun List<String>.toVOEasyToWinHeroes(): VOEasyToWinHeroes = VOEasyToWinHeroes(
    map { VOHeroFlowItem(it, Repository.getFullUrlHeroImage(it)) }
)
