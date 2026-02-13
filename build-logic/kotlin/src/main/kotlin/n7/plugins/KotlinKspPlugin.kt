package n7.plugins

import org.gradle.api.Plugin
import org.gradle.api.Project

class KotlinKspPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        target.plugins.run {
            apply("com.google.devtools.ksp")
        }
    }
}
