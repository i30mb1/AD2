package n7.ad2.parseinfo

import java.io.File
import java.net.URL
import javax.imageio.ImageIO

internal val assets = System.getProperty("user.dir") + "\\app\\src\\main\\assets\\"
internal val assetsDatabase = System.getProperty("user.dir") + "/core/database/src/main/assets"
internal val assetsDatabaseItems = "$assetsDatabase/items"

internal fun String.removeBrackets(): String {
    return removeSurrounding("(", ")")
}

internal fun saveImage(url: String, path: String, fileName: String) {
    try {
        val bufferImageIO = ImageIO.read(URL(url))
        val directory = File(path)
        directory.mkdirs()
        val file = File(path, "$fileName.png")
        val fileWebp = File(path, "$fileName.webp")
        if (file.exists() || fileWebp.exists()) return
        file.createNewFile()
        ImageIO.write(bufferImageIO, "png", file)
        println("image '$path' saved")
    } catch (e: Exception) {
        println("image for $path not saved, $e")
    }
}

internal fun deleteFolderInAssets(path: String) {
    val file = File(assets + path)
    if (file.exists()) return
    file.deleteRecursively()
}

internal fun saveFile(path: String, fileName: String, text: String) {
    val directory = File(path)
    directory.mkdirs()
    val file = File(path, fileName)
    file.createNewFile()
    file.writeText(text)
    println("file (${file.length()} bytes) saved in '$path'")
}