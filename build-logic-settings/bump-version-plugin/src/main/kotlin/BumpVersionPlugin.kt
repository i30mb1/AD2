import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.register

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