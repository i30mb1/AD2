import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.register

class BumpVersionPlugin : Plugin<Project> {

    override fun apply(target: Project) = with(target) {
        val extension = extensions.create(
            "bumpVersionConfig",
            BumpVersionExtension::class.java,
            this
        )
        tasks.register("bumpVersion", BumpVersionTask::class) {
            bumpEnabled.set(extension.isEnabled)
            group = "n7"
            inputFile.set(file("version.properties"))
            outPutFile.set(file("version.properties"))
            layout.projectDirectory
        }
        Unit
    }

}
