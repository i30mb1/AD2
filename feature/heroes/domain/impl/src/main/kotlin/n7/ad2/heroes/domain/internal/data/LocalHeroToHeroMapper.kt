package n7.ad2.heroes.domain.internal.data

import n7.ad2.database_guides.internal.model.HeroDb
import n7.ad2.heroes.domain.internal.getFullUrlHeroImage
import n7.ad2.heroes.domain.model.Hero

internal object LocalHeroToHeroMapper : (HeroDb) -> Hero {

    override operator fun invoke(from: HeroDb) = Hero(
        from.name,
        getFullUrlHeroImage(from.name.lowercase()),
        from.viewedByUser,
        from.mainAttr,
    )
}
