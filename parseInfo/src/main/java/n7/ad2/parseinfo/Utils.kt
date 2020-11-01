package n7.ad2.parseinfo

import java.io.File
import java.net.URL
import javax.imageio.ImageIO

internal val assets = System.getProperty("user.dir") + "\\app\\src\\main\\assets\\"
internal const val HERO_FULL_PHOTO_TYPE = "png"

internal fun String.removeBrackets(): String {
    val regex = Regex(" \\(.+?\\)")
    val matches = regex.containsMatchIn(this)
    val result = if (matches) this.replace(regex, "") else this
    return result.trim()
}

internal fun saveImage(url: String, directoryInAssets: String, fileName: String) {
    try {
        val bufferImageIO = ImageIO.read(URL(url))
        val file = File(assets + directoryInAssets + File.separator + "$fileName.$HERO_FULL_PHOTO_TYPE")
        file.mkdirs()
        ImageIO.write(bufferImageIO, HERO_FULL_PHOTO_TYPE, file)
        println("image -$directoryInAssets saved")
    } catch (e: Exception) {
        println("image full not saved")
    }
}
