import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.register
import java.io.File
import java.io.FileInputStream
import java.io.InputStreamReader
import java.util.Properties

class BumpVersionPlugin : Plugin<Project> {

    override fun apply(target: Project) = with(target) {
        tasks.register("bumpVersion", BumpVersionTask::class) {
            group = "n7"
            inputFile.set(file("input.txt"))
            outPutFile.set(file("output.txt"))
            layout.projectDirectory
        }
        Unit
    }

}

fun isCI(): Boolean {
    val properties = Properties()
    val localProperties = File("local.properties")
    if (localProperties.isFile) {
        InputStreamReader(FileInputStream(localProperties), Charsets.UTF_8).use { reader ->
            properties.load(reader)
        }
    } else error("File local.properties not found")

    return properties.getProperty("IS_CI").toBoolean()
}