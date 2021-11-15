package n7.ad2.ui.heroes.domain.vo

import n7.ad2.base.adapter.HeaderViewHolder

sealed class VOHero
data class VOHeroBody(val name: String, val imageUrl: String, val viewedByUser: Boolean) : VOHero()
data class VOHeroHeader(val data: HeaderViewHolder.Data) : VOHero()