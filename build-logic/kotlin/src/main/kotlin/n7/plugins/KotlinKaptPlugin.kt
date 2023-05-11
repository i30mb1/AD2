package n7.plugins

import org.gradle.api.Plugin
import org.gradle.api.Project

class KotlinKaptPlugin: Plugin<Project> {

    override fun apply(target: Project) {
        target.plugins.run {
            apply("kotlin-kapt")
        }
    }

}