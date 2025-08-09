import javax.inject.Inject
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import org.gradle.api.GradleException

abstract class MeasureBuildTask @Inject constructor() : DefaultTask() {

    @TaskAction
    open fun action() {
        try {
            val isWindows = System.getProperty("os.name").lowercase().contains("windows")
            val executableName = if (isWindows) "gradle-profiler.bat" else "gradle-profiler"
            val executablePath = project.file("gradle/profiler/bin/$executableName")
            
            if (!executablePath.exists()) {
                throw GradleException("gradle-profiler executable not found at: ${executablePath.absolutePath}")
            }
            
            val processBuilder = ProcessBuilder(
                executablePath.absolutePath,
                "--benchmark",
                "--output-dir",
                "gradle/profiler/last-output",
                "--scenario-file",
                "gradle/profiler/profiler.scenarios",
                "incremental"
            )
            
            processBuilder.directory(project.projectDir)
            processBuilder.inheritIO()
            
            val process = processBuilder.start()
            val exitCode = process.waitFor()
            
            if (exitCode != 0) {
                throw GradleException("gradle-profiler execution failed with exit code: $exitCode")
            }
            
            val csvFile = project.file("gradle/profiler/last-output/benchmark.csv")
            if (!csvFile.exists()) {
                throw GradleException("Benchmark CSV file not found at: ${csvFile.absolutePath}")
            }
            
            val measuredBuilds = csvFile.readLines()
                .filter { it.startsWith("measured build #") }
                .mapNotNull { line ->
                    val parts = line.split(",")
                    if (parts.size >= 2) {
                        parts[1].trim().toIntOrNull()
                    } else null
                }
            
            if (measuredBuilds.isEmpty()) {
                throw GradleException("No measured build times found in CSV file")
            }
            
            val average = measuredBuilds.average().toInt()
            val measureFile = project.file("gradle/profiler/measure.txt")
            
            measureFile.parentFile.mkdirs()
            if (!measureFile.exists()) {
                measureFile.createNewFile()
            }
            
            measureFile.appendText("Execution time $average ms\n")
            println("Build measurement completed. Average execution time: $average ms")
            
        } catch (e: Exception) {
            throw GradleException("Failed to measure build time: ${e.message}", e)
        }
    }

}
