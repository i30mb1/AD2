import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.register

class MeasureBuildPlugin : Plugin<Project> {

    override fun apply(target: Project) = with(target) {
        tasks.register("measureBuild", MeasureBuildTask::class) {
            group = "n7"
        }
        Unit
    }

}