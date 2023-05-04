import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.register

class BumpVersionPlugin : Plugin<Project> {

    override fun apply(target: Project) = with(target) {
        tasks.register("bumpVersion", BumpVersionTask::class) {
            group = "n7"
            inputFile.set(file("version.properties"))
            outPutFile.set(file("version.properties"))
            layout.projectDirectory
        }
        Unit
    }

}