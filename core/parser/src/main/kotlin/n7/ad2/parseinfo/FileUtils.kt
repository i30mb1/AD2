package n7.ad2.parseinfo

import java.io.File

internal fun saveFile(path: String, fileName: String, text: String): Boolean {
    val pathFormatted = path.lowercase().replace(" ", "_")
    return saveFileInternal(pathFormatted, fileName, text)
}

private fun saveFileInternal(path: String, fileName: String, text: String): Boolean {
    val directory = File(path)
    directory.mkdirs()
    val file = File(path, fileName)
    file.createNewFile()
    val oldFileSize = file.readText().length
    val newSize = text.length
    if (newSize <= 0) error("empty file for $path")
    if (oldFileSize == newSize) return false
    file.writeText(text)
    return true
}