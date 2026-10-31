package n7.ad2.games.internal.games.show

import android.app.Application
import org.json.JSONObject
import java.util.Locale
import javax.inject.Inject

/** Предмет из assets: [key] - папка `items/<key>`, [components] - ключи компонентов (могут повторяться) */
internal data class RecipeItem(val key: String, val title: String, val cost: Int, val components: List<String>) {
    val imageUrl: String get() = "file:///android_asset/items/$key/full.webp"
}

/**
 * Читает цены и рецепты предметов прямо из assets базы (`items/<key>/<lang>/description.json`, поля cost и consistFrom).
 * Список один на процесс: 240 файлов читаются один раз.
 */
internal class ItemRecipes @Inject constructor(private val application: Application) {

    fun all(): List<RecipeItem> = cache ?: synchronized(ItemRecipes) { cache ?: load().also { cache = it } }

    private fun load(): List<RecipeItem> {
        val assets = application.assets
        val lang = if (Locale.getDefault().language == "ru") "ru" else "en"
        return assets.list("items").orEmpty().sorted().mapNotNull { key ->
            val json = runCatching {
                val path = "items/$key/$lang/description.json".takeIf { assets.list("items/$key/$lang")?.isNotEmpty() == true } ?: "items/$key/en/description.json"
                JSONObject(assets.open(path).bufferedReader().use { it.readText() })
            }.getOrNull() ?: return@mapNotNull null
            val components = json.optJSONArray("consistFrom")?.let { array -> List(array.length()) { array.getString(it) } }.orEmpty()
            RecipeItem(key, json.optString("name", key), json.optInt("cost", 0), components)
        }
    }

    private companion object {
        @Volatile var cache: List<RecipeItem>? = null
    }
}
