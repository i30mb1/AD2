package n7.ad2.parseinfo

import java.io.File
import java.net.URL
import javax.imageio.ImageIO

internal fun saveImage(url: String, path: String, name: String) {
    saveImageInternal(url, path.lowercase().replace(" ", "_"), name)
}

private fun saveImageInternal(url: String, path: String, name: String) {
    val bufferImageIO = ImageIO.read(URL(url))
    val directory = File(path)
    directory.mkdirs()
    val file = File(path, "$name.png")
    val fileWebp = File(path, "$name.webp")
    if (file.exists() || fileWebp.exists()) return
    file.createNewFile()
    ImageIO.write(bufferImageIO, "png", file)
    println("image $name downloaded")
}