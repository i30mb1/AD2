import org.gradle.api.Project
import java.io.File
import java.io.FileInputStream
import java.io.InputStreamReader
import java.util.Properties

fun Project.isCI(): Boolean {
    val properties = Properties()
    val localProperties = File("local.properties")
    if (localProperties.isFile) {
        InputStreamReader(FileInputStream(localProperties), Charsets.UTF_8).use { reader ->
            properties.load(reader)
        }
    } else error("File local.properties not found")

    return properties.getProperty("IS_CI").toBoolean()
}