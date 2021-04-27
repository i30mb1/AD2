package n7.ad2.ui.heroes.domain.adapter

import n7.ad2.data.source.local.HeroRepository
import n7.ad2.data.source.local.model.LocalHero
import n7.ad2.ui.heroes.domain.vo.VOHero

fun LocalHero.toVO(): VOHero {
    return VOHero(
        name,
        HeroRepository.getFullUrlHeroImage(name),
        viewedByUser,
    )
}