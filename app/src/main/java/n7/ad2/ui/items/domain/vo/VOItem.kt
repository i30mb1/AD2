package n7.ad2.ui.items.domain.vo

import n7.ad2.base.adapter.HeaderViewHolder

sealed class VOItem
data class VOItemBody(val name: String, val imageUrl: String, val viewedByUser: Boolean) : VOItem()
data class VOItemHeader(val data: HeaderViewHolder.Data) : VOItem()