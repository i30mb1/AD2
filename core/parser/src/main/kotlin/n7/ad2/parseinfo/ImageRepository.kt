package n7.ad2.parseinfo

import java.io.File

internal val assetsDatabase = System.getProperty("user.dir") + "/core/database/src/main/assets"
internal val assetsDatabaseItems = "$assetsDatabase/items"
internal val assetsDatabaseSpells = "$assetsDatabase/images"
internal val assetsDatabaseHeroes = "$assetsDatabase/heroes"

internal data class Image(val path: String, val name: String)

internal object ImageRepository {

    private lateinit var spells: List<Image>
    private lateinit var items: List<Image>
    private lateinit var heroes: List<Image>

    init {
        update()
    }

    fun update() {
        heroes = File(assetsDatabaseHeroes).listFiles()?.mapNotNull { file ->
            val file = file.listFiles()?.find { it.name.contains("minimap") } ?: return@mapNotNull null
            val extension = file.name.substringAfter(".")
            val path = file.path.substringAfter("assets\\").replace("\\", "/") + "/minimap.$extension"
            val name = file.name + "_minimap"
            Image(path, name)
        } ?: emptyList()
        spells = File(assetsDatabaseSpells).listFiles()?.map { file ->
            val name = file.name.substringBefore(".")
            val path = file.path.substringAfter("assets\\").replace("\\", "/")
            Image(path, name)
        } ?: emptyList()
        items = File(assetsDatabaseItems).listFiles()?.map { file ->
            val path = file.path.substringAfter("assets\\").replace("\\", "/") + "/full.webp"
            Image(path, file.name)
        } ?: emptyList()
    }

    fun getImages(): List<Image> = spells + items

}