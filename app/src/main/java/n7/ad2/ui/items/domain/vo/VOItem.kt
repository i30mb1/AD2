package n7.ad2.ui.items.domain.vo

sealed class VOItem
data class VOItemBody(val name: String, val image: String, val viewedByUser: Boolean) : VOItem()
data class VOItemHeader(val title: String) : VOItem()