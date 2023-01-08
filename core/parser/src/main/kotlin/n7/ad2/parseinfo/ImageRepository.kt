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

    init {
        update()
    }

    fun update() {
        spells = File(assetsDatabaseSpells).listFiles()?.map {
            val name = it.name.substringBefore(".")
            val path = it.path.substringAfter("assets\\").replace("\\", "/")
            Image(path, name)
        } ?: emptyList()
        items = File(assetsDatabaseItems).listFiles()?.map { file ->
            val name = file.name
            val path = file.path.substringAfter("assets\\").replace("\\", "/") + "/full.webp"
            Image(path, name)
        } ?: emptyList()
    }

    fun getImages(): List<Image> = spells + items

}