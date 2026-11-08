package n7.ad2.feature.heroes.ui.internal.domain.vo

import n7.ad2.heroes.domain.model.HeroAttribute

internal sealed class VOHero {
    data class Body(val name: String, val imageUrl: String, val viewedByUser: Boolean) : VOHero()
    data class Header(val attribute: HeroAttribute) : VOHero()
}
