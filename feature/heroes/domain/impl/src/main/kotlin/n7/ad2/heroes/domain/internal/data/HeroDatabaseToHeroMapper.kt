package n7.ad2.heroes.domain.internal.data

import n7.ad2.heroes.domain.internal.data.db.HeroDatabase
import n7.ad2.heroes.domain.model.Hero

internal object HeroDatabaseToHeroMapper : (HeroDatabase) -> Hero {

    override operator fun invoke(from: HeroDatabase) = Hero(
        from.name,
        "file:///android_asset/heroes/${from.name.lowercase().replace(" ", "_")}/full.png",
        from.viewedByUser,
        from.mainAttr,
    )
}
