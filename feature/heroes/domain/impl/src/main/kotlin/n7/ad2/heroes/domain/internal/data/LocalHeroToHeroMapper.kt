package n7.ad2.heroes.domain.internal.data

import n7.ad2.database_guides.internal.model.LocalHero
import n7.ad2.heroes.domain.internal.getFullUrlHeroImage
import n7.ad2.heroes.domain.model.Hero

internal object LocalHeroToHeroMapper : (LocalHero) -> Hero {

    override operator fun invoke(from: LocalHero) = Hero(
        from.name,
        getFullUrlHeroImage(from.name.lowercase()),
        from.viewedByUser,
        from.mainAttr,
    )
}