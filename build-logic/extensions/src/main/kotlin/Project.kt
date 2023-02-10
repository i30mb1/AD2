import org.gradle.accessors.dm.LibrariesForLibs
import org.gradle.api.Project
import org.gradle.kotlin.dsl.the
import java.io.File
import java.io.FileInputStream
import java.io.InputStreamReader
import java.util.Properties

const val applicationID = "n7.ad2"

fun Project.getVersionCode(): Int {
    val properties = getProperties("version.properties")
    val major = properties.getProperty("MAJOR", "1").toInt() * 1000
    val minor = properties.getProperty("MINOR", "0").toInt() * 10
    val patch = properties.getProperty("PATCH", "0").toInt()
    return major + minor + patch
}

fun Project.getVersionName(): String {
    val properties = getProperties("version.properties")
    val major = properties.getProperty("MAJOR", "1").toInt()
    val minor = properties.getProperty("MINOR", "0").toInt()
    val patch = properties.getProperty("PATCH", "0").toInt()
    return "$major.$minor.$patch"
}

fun Project.isCI(): Boolean {
    val properties = Properties()
    val file = File("local.properties")
    if (file.isFile) {
        InputStreamReader(FileInputStream(file), Charsets.UTF_8).use { reader ->
            properties.load(reader)
        }
    } else {
        return false
    }
    return properties.getProperty("IS_CI", "false").toBoolean()
}

/**
 * workaround to make version catalog accessible in convention plugins
 * https://github.com/gradle/gradle/issues/15383
 */
val Project.libs get() = the<LibrariesForLibs>()

private fun Project.getProperties(fileName: String): Properties {
    val properties = Properties()
    val file = File(fileName)
    if (!file.exists()) {
        logger.error("File $rootDir\\$fileName not found!")
        return properties
    }
    InputStreamReader(FileInputStream(file), Charsets.UTF_8).use { reader ->
        properties.load(reader)
    }

    return properties
}