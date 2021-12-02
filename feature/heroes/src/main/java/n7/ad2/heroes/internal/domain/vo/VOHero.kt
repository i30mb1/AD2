package n7.ad2.heroes.internal.domain.vo

import n7.ad2.ui.adapter.HeaderViewHolder

sealed class VOHero {
    data class Body(val name: String, val imageUrl: String, val viewedByUser: Boolean) : VOHero()
    data class Header(val data: HeaderViewHolder.Data) : VOHero()
}
