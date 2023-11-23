package n7.plugins

import java.io.File
import java.io.FileInputStream
import java.io.InputStreamReader
import java.util.Properties
import kotlin.system.measureTimeMillis
import kotlin.time.Duration.Companion.milliseconds
import org.gradle.api.Plugin
import org.gradle.api.initialization.Settings

class IncludeModulesPlugin : Plugin<Settings> {

    private val dirsNamesWithoutModules = listOf("build-logic", "build")
    private val ignoreModules = listOf("AD2", "rules")

    override fun apply(target: Settings) = with(target) {
        includeAllModules()
    }

    private fun Settings.includeAllModules() {
        val count: Int
        val time = measureTimeMillis {
            count = includeAllModule()
        }.milliseconds
        println("AD2: Included $count modules in $time")
    }

    private fun Settings.includeAllModule(): Int {
        val modules = getAllModules()
        modules.forEach { include(it.path) }
        return modules.size
    }

    private fun Settings.getAllModules(): List<ModuleData> {
        return rootDir.walk().maxDepth(5)
            .onEnter { it.isSuitableFile() }
            .filter { it.hasBuildGradleFile() && ignoreModules.contains(it.name).not() }
            .map {
                ModuleData(
                    it.path.substringAfterLast("AD2").replace("\\", ":"),
                    it,
                    it.buildGradleFile(),
                )
            }
            .toList()
    }

    private fun File.buildGradleFile(): File {
        val buildGradleFile = File(this, "build.gradle")
        return if (buildGradleFile.isFile) {
            buildGradleFile
        } else {
            File(this, "build.gradle.kts")
        }
    }

    private fun readRootModule(rootDir: File): String {
        val properties = getLocalProperties(rootDir)
        return properties.getProperty("ROOT_MODULE", "")
    }

    private fun getLocalProperties(root: File): Properties {
        val properties = Properties()
        val file = File(root, "local.properties")
        if (file.isFile) {
            InputStreamReader(FileInputStream(file), Charsets.UTF_8).use { reader ->
                properties.load(reader)
            }
        }
        return properties
    }

//    private fun findModules(file: File) {
//        file.walk().count { it.isDirectory && it.hasBuildGradleFile() }
//        if (file.isSuitableFile()) {
//            if (file.hasBuildGradleFile()) {
//                include(":${file.path.replace("\\", ":")}")
//            } else {
//                file.listFiles()
//                    .orEmpty()
//                    .forEach { subFile -> includeModule(subFile) }
//            }
//        }
//    }

    private fun File.hasSettingsGradleFile(): Boolean {
        return File(this, "settings.gradle").isFile || File(this, "settings.gradle.kts").isFile
    }

    private fun File.hasBuildGradleFile(): Boolean {
        return File(this, "build.gradle").isFile || File(this, "build.gradle.kts").isFile
    }

    private fun File.isSuitableFile(): Boolean {
        return name.startsWith(".").not() && isDirectory && isHidden.not() && dirsNamesWithoutModules.contains(name).not()
    }

    private data class ModuleData(
        val path: String,
        val directory: File,
        val buildGradleFile: File,
    )
}
