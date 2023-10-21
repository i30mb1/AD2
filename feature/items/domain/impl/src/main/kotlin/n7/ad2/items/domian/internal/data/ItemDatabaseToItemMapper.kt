package n7.ad2.items.domian.internal.data

import n7.ad2.items.domain.model.Item
import n7.ad2.items.domian.internal.data.db.ItemDatabase

internal object ItemDatabaseToItemMapper : (ItemDatabase) -> Item {

    override operator fun invoke(from: ItemDatabase) = Item(
        from.name,
        "file:///android_asset/images/${from.name.lowercase().replace(" ", "_")}.webp",
        from.type,
        from.viewedByUser,
    )
}
