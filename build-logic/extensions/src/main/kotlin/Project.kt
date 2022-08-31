import org.gradle.accessors.dm.LibrariesForLibs
import org.gradle.api.Project
import org.gradle.kotlin.dsl.the
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

/**
 * workaround to make version catalog accessible in convention plugins
 * https://github.com/gradle/gradle/issues/15383
 */
val Project.libs get() = the<LibrariesForLibs>()