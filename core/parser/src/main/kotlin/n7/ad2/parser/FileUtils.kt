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
    val newSize = text.length
    if (newSize <= 0) error("empty file for $path")
    val oldFileSize = file.readText().length
    if (oldFileSize == newSize) return false
    file.writeText(text)
    return true
}
