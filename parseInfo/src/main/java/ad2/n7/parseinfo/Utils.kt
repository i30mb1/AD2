package ad2.n7.parseinfo

import java.io.File
import java.net.URL
import javax.imageio.ImageIO

val assets = System.getProperty("user.dir") + "\\app\\src\\main\\assets\\"
const val HERO_FULL_PHOTO_TYPE = "png"

fun saveImage(url: String, directoryInAssets: String, fileName: String) {
    try {
        val bufferImageIO = ImageIO.read(URL(url))
        val file = File(assets + directoryInAssets + File.separator + "$fileName.$HERO_FULL_PHOTO_TYPE")
        file.mkdirs()
        ImageIO.write(bufferImageIO, HERO_FULL_PHOTO_TYPE, file)
        println("image full saved")
    } catch (e: Exception) {
        println("image full not saved")
    }
}
