package n7.plugins

import org.gradle.api.Plugin
import org.gradle.api.initialization.Settings

class SettingsPlugin: Plugin<Settings> {

    override fun apply(target: Settings) {
        target.plugins.run {
            apply("n7.plugins.include-modules")
            apply("n7.plugins.git-hooks")
            apply("convention.settings")
        }
    }

}