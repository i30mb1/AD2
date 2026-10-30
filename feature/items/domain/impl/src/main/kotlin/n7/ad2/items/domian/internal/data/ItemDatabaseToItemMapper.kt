package n7.ad2.items.domian.internal.data

import n7.ad2.items.domain.model.Item
import n7.ad2.items.domian.internal.data.db.ItemDatabase

/** [titles] is `items/names.json` produced by core/parser: asset key → display name. */
internal class ItemDatabaseToItemMapper(private val titles: Map<String, String>) : (ItemDatabase) -> Item {

    override operator fun invoke(from: ItemDatabase): Item {
        val key = from.name.lowercase().replace(" ", "_")
        return Item(
            from.name,
            titles[key] ?: key.toTitle(),
            "file:///android_asset/items/$key/full.webp",
            from.type,
            from.viewedByUser,
        )
    }

    private fun String.toTitle(): String = split('_').joinToString(" ") { word -> word.replaceFirstChar(Char::uppercaseChar) }
}
