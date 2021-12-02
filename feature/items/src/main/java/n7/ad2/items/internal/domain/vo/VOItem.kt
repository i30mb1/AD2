package n7.ad2.items.internal.domain.vo

import n7.ad2.ui.adapter.HeaderViewHolder

sealed class VOItem {
    data class Body(val name: String, val imageUrl: String, val viewedByUser: Boolean) : VOItem()
    data class Header(val data: HeaderViewHolder.Data) : VOItem()
}
