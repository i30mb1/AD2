package n7.ad2.parseinfo

import java.io.File

internal fun saveFile(path: String, fileName: String, text: String) {
    val pathFormatted = path.lowercase().replace(" ", "_")
    saveFileInternal(pathFormatted, fileName, text)
}

private fun saveFileInternal(path: String, fileName: String, text: String) {
    val directory = File(path)
    directory.mkdirs()
    val file = File(path, fileName)
    file.createNewFile()
    file.writeText(text)
    if (file.length() == 0L) error("empty file for $path")
    println("file $fileName saved")
}