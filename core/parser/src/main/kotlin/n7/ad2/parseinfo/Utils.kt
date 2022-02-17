package n7.ad2.parseinfo

import java.io.File
import java.net.URL
import javax.imageio.ImageIO

internal val assets = System.getProperty("user.dir") + "\\app\\src\\main\\assets\\"
internal val assetsDatabase = System.getProperty("user.dir") + "\\core\\database\\src\\main\\assets\\"
internal const val HERO_FULL_PHOTO_TYPE = "png"

internal fun String.removeBrackets(): String {
    return removeSurrounding("(", ")")
}

internal fun saveImage(url: String, directoryInAssets: String, fileName: String) {
    try {
        val bufferImageIO = ImageIO.read(URL(url))
        val file = File(assets + directoryInAssets + File.separator + "$fileName.$HERO_FULL_PHOTO_TYPE")
        file.mkdirs()
        ImageIO.write(bufferImageIO, HERO_FULL_PHOTO_TYPE, file)
        println("image '$directoryInAssets' saved")
    } catch (e: Exception) {
        println("image for $directoryInAssets not saved")
    }
}

internal fun deleteFolderInAssets(path: String) {
    val file = File(assets + path)
    if (file.exists()) return
    file.deleteRecursively()
}

internal fun saveFileWithDataInAssets(path: String, text: String) {
    val file = File(assetsDatabase + path)
    file.writeText(text)
    println("file (${file.length()} bytes) saved in '$path'")
}

internal fun createFolderInAssets(path: String) {
    val file = File(assets + path)
    if (file.exists()) return
    file.mkdirs()
    println("path to '$path' created")
}
