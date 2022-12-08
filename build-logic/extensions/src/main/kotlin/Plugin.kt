import java.io.File

fun Any.getResource(filename: String): File? {
    val input = this::class.java.classLoader.getResourceAsStream(filename) ?: return null

    val tempFile = File.createTempFile(
        filename.substringBeforeLast('.'),
        "." + filename.substringAfterLast('.')
    )
    tempFile.deleteOnExit()

    tempFile.writer().use { output ->
        input.bufferedReader().use { input ->
            output.write(input.readText())
        }
    }

    return tempFile
}