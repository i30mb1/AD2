package n7.plugins

import org.gradle.api.Plugin
import org.gradle.api.initialization.Settings

class GitHooksPlugin: Plugin<Settings> {

    override fun apply(target: Settings) = with(target) {
       val output = providers.exec {
            commandLine("git", "config", "core.hooksPath", ".githooks")
        }
        println("setup core.hooksPath: exitValue=${output.result.orNull?.exitValue}")
    }

}