package n7.ad2.items.domain.model

/** [name] is the asset key (`items/<name>/...`), [title] is what the user sees. */
class Item(val name: String, val title: String, val imageUrl: String, val type: String, val viewedByUser: Boolean)
