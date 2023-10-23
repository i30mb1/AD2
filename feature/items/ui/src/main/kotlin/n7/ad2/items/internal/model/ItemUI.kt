package n7.ad2.items.internal.model

import n7.ad2.ui.adapter.HeaderViewHolder

internal sealed class ItemUI {
    data class Body(val name: String, val imageUrl: String, val viewedByUser: Boolean) : ItemUI()
    data class Header(val data: HeaderViewHolder.Data) : ItemUI()
}
