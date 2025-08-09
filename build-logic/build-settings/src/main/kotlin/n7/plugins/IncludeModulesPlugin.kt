package n7.plugins

import java.io.File
import java.io.FileInputStream
import java.io.InputStreamReader
import java.util.Properties
import kotlin.system.measureTimeMillis
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.DurationUnit
import org.gradle.api.Plugin
import org.gradle.api.initialization.Settings

class IncludeModulesPlugin : Plugin<Settings> {

    private val dirsNamesWithoutModules = listOf("build-logic", "build")
    private val ignoreModules = listOf("AD2")

    override fun apply(target: Settings) = with(target) {
        val targetModule = readTargetModule(rootDir)
        if (targetModule.isNotEmpty()) {
            includeSpecificModule(targetModule)
        } else {
            includeAllModules()
        }
    }

    private fun Settings.includeAllModules() {
        val count: Int
        val time = measureTimeMillis {
            count = includeAllModule()
        }.milliseconds
        println("AD2: Included $count modules in ${time.toString(DurationUnit.SECONDS, 2)}")
    }
    
    private fun Settings.includeSpecificModule(targetModule: String) {
        val count: Int
        val time = measureTimeMillis {
            count = includeModuleWithDependencies(targetModule)
        }.milliseconds
        println("AD2: Included $count modules for target '$targetModule' in ${time.toString(DurationUnit.SECONDS, 2)}")
    }

    private fun Settings.includeAllModule(): Int {
        val modules = getAllModules()
        modules.forEach { include(it.module) }
        return modules.size
    }

    private fun Settings.includeModuleWithDependencies(targetModule: String): Int {
        val allModules = getAllModules()
        val moduleByPath = allModules.associateBy { it.module }
        
        
        val targetModuleData = moduleByPath[targetModule]
        if (targetModuleData == null) {
            println("AD2: Target module '$targetModule' not found!")
            return 0
        }
        
        val requiredModules = findAllDependencies(targetModuleData, moduleByPath, mutableSetOf())
        requiredModules.add(targetModuleData) // Добавляем сам целевой модуль
        
        requiredModules.forEach { module ->
            include(module.module)
        }
        
        return requiredModules.size
    }
    
    private fun findAllDependencies(
        module: ModuleData,
        allModules: Map<String, ModuleData>,
        visited: MutableSet<String>
    ): MutableSet<ModuleData> {
        val dependencies = mutableSetOf<ModuleData>()
        
        if (visited.contains(module.module)) {
            return dependencies
        }
        
        visited.add(module.module)
        
        // Извлекаем зависимости из build.gradle файла
        val moduleDependencies = extractDependenciesFromBuildFile(module.path)
        
        moduleDependencies.forEach { depPath ->
            val depModule = allModules[depPath]
            if (depModule != null) {
                dependencies.add(depModule)
                dependencies.addAll(findAllDependencies(depModule, allModules, visited))
            }
        }
        
        return dependencies
    }
    
    private fun extractDependenciesFromBuildFile(moduleDir: File): List<String> {
        val buildFile = moduleDir.buildGradleFile()
        if (!buildFile.exists()) return emptyList()
        
        val dependencies = mutableListOf<String>()
        val lines = buildFile.readLines()
        
        lines.forEach { line ->
            val trimmedLine = line.trim()
            
            // Ищем строки с projects. 
            if (trimmedLine.contains("(projects.") && 
                (trimmedLine.contains("implementation") || 
                 trimmedLine.contains("api") || 
                 trimmedLine.contains("compileOnly") ||
                 trimmedLine.contains("runtimeOnly"))) {
                
                val projectReference = extractProjectReference(trimmedLine)
                if (projectReference.isNotEmpty()) {
                    dependencies.add(projectReference)
                }
            }
            
            // Ищем строки с project(...) для обычных зависимостей
            if (trimmedLine.contains("project(\":") && 
                (trimmedLine.contains("implementation") || 
                 trimmedLine.contains("api") || 
                 trimmedLine.contains("compileOnly") ||
                 trimmedLine.contains("runtimeOnly") ||
                 trimmedLine.contains("add("))) {
                
                val projectReference = extractDirectProjectReference(trimmedLine)
                if (projectReference.isNotEmpty()) {
                    dependencies.add(projectReference)
                }
            }
        }
        
        // Для любого модуля, который использует convention плагины, добавляем core:rules
        if (lines.any { it.contains("convention.android") }) {
            dependencies.add(":core:rules")
        }
        
        return dependencies.distinct()
    }
    
    private fun extractDirectProjectReference(line: String): String {
        // Извлекаем ссылку на проект из строки типа "add("lintChecks", project(":core:rules"))"
        val regex = Regex("project\\(\"([^\"]+)\"\\)")
        val match = regex.find(line)
        return match?.groupValues?.get(1) ?: ""
    }
    
    private fun extractProjectReference(line: String): String {
        // Извлекаем ссылку на проект из строки типа "implementation(projects.core.commonAndroid)"
        val regex = Regex("projects\\.([a-zA-Z0-9_.]+)")
        val match = regex.find(line)
        return if (match != null) {
            val projectPath = match.groupValues[1].replace(".", ":")
            ":" + convertCamelCaseToKebabCase(projectPath)
        } else {
            ""
        }
    }
    
    private fun convertCamelCaseToKebabCase(input: String): String {
        return input.split(":").joinToString(":") { segment ->
            segment.replace(Regex("([a-z])([A-Z])"), "$1-$2").lowercase()
        }
    }

    private fun Settings.getAllModules(): List<ModuleData> {
        return rootDir.walk().maxDepth(5)
            .onEnter { it.isSuitableFile() }
            .filter { it.hasBuildGradleFile() && ignoreModules.contains(it.name).not() }
            .map {
                ModuleData(
                    it.path.substringAfterLast("AD2").replace(File.separator, ":"),
                    it,
                    getProjects(it),
                )
            }.toList()
    }

    private fun getProjects(file: File): List<ModuleData> {
        return file.buildGradleFile().readLines().filter {
            it.contains("(projects.")
        }.map {
            ModuleData(
                ":" + it.substringAfter(".").dropLast(1).replace(".", ":").toSnakeCase(),
                file,
            )
        }
    }

    private fun File.buildGradleFile(): File {
        val buildGradleFile = File(this, "build.gradle")
        return if (buildGradleFile.isFile) {
            buildGradleFile
        } else {
            File(this, "build.gradle.kts")
        }
    }

    private fun readTargetModule(rootDir: File): String {
        val properties = getLocalProperties(rootDir)
        return properties.getProperty("module", "").trim().removeSurrounding("\"")
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

    /**
     * Функция которая преобразует camelCase -> camel-case
     */
    private fun String.toSnakeCase() = replace("(?<=.)(?=\\p{Upper})".toRegex(), "-").lowercase()

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
        val module: String,
        val path: File,
        val includedModules: List<ModuleData> = emptyList(),
    )
}
