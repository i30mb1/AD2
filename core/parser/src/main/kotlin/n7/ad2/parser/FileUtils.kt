package n7.ad2.parser

import java.io.File

internal fun saveFile(path: String, fileName: String, text: String): Boolean {
    val pathFormatted = path.lowercase().replace(" ", "_")
    val fileNameFormatted = fileName.lowercase()
    return saveFileInternal(pathFormatted, fileNameFormatted, text)
}

private fun saveFileInternal(path: String, fileName: String, text: String): Boolean {
    val directory = File(path)
    directory.mkdirs()
    val file = File(path, fileName)
    file.createNewFile()
    if (text.isEmpty()) error("empty file for $path")
    val newBytes = text.toByteArray(Charsets.UTF_8)
    val oldBytes = file.readBytes()
    if (oldBytes.contentEquals(newBytes)) return false
    file.writeBytes(newBytes)
    return true
}
