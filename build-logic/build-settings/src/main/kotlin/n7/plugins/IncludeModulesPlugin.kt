package n7.plugins

import org.gradle.api.Plugin
import org.gradle.api.initialization.Settings
import java.io.File
import kotlin.system.measureNanoTime
import kotlin.system.measureTimeMillis
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.nanoseconds
import kotlin.time.Duration.Companion.seconds

class IncludeModulesPlugin : Plugin<Settings> {

    private val baseModuleFolders = setOf("core", "feature")
    private val ignoreModules = listOf("rules")

    override fun apply(target: Settings) = with(target) {
        includeAllModules()
    }

    private fun Settings.includeAllModules() {
       val time = measureTimeMillis {
           for (folder in baseModuleFolders) includeModule(File(folder))
        }.milliseconds
        println("Included project modules in $time")
    }

    private fun Settings.includeModule(file: File) {
        if (file.isSuitableFile()) {
            if (file.hasBuildGradleFile()) {
                include(":${file.path.replace("\\", ":")}")
            } else {
                file.listFiles()
                    .orEmpty()
                    .forEach { subFile -> includeModule(subFile) }
            }
        }
    }

    private fun File.hasSettingsGradleFile(): Boolean {
        return File(this, "settings.gradle").isFile || File(this, "settings.gradle.kts").isFile
    }

    private fun File.hasBuildGradleFile(): Boolean {
        return File(this, "build.gradle").isFile || File(this, "build.gradle.kts").isFile
    }

    private fun File.isSuitableFile(): Boolean {
        return name.startsWith(".").not() && isDirectory && isHidden.not() && ignoreModules.contains(name).not()
    }
}

private data class ModuleData(
    val path: String,
    val directory: File,
    val buildGradleFile: File,
)
