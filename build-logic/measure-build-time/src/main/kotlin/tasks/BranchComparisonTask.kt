package tasks

import BranchComparisonExtension
import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction
import org.gradle.process.ExecOperations
import javax.inject.Inject

/**
 * Advanced branch comparison task using gradle-profiler.
 *
 * Features:
 * - Professional benchmark reports with charts
 * - Git integration for automatic branch switching
 * - Statistical analysis with warm-up runs
 * - HTML and CSV output formats
 */
abstract class BranchComparisonTask @Inject constructor() : DefaultTask() {

    @get:Input
    abstract val extension: Property<BranchComparisonExtension>

    @get:Inject
    @get:Internal
    abstract val execOperations: ExecOperations

    @TaskAction
    fun execute() {
        val config = extension.get()
        val originalBranch = getCurrentBranch()
        val baseBranch = config.baseBranch.get()

        println("🔀 Comparing branch performance...")
        println("📊 Current branch: $originalBranch")
        println("📊 Baseline: $baseBranch")
        println("⚙️ Tasks: ${config.targetTasks.get().joinToString(", ")}")

        if (originalBranch == baseBranch) {
            throw GradleException("⚠️ Cannot compare - already on base branch '$baseBranch'!")
        }

        val profilerBin = getProfilerExecutable()
        val scenariosFile = createDynamicScenarios(originalBranch, baseBranch, config)

        try {
            runBenchmark(profilerBin, scenariosFile, config)
            reportResults()
        } catch (e: Exception) {
            println("❌ Branch comparison failed: ${e.message}")
            println("💡 Make sure gradle-profiler is available and branches exist")
            throw GradleException("Benchmark execution failed", e)
        } finally {
            // Return to original branch
            returnToOriginalBranch(originalBranch)
            // Cleanup temporary files
            scenariosFile.delete()
        }
    }

    private fun getCurrentBranch(): String = project.providers.exec {
        commandLine("git", "rev-parse", "--abbrev-ref", "HEAD")
    }.standardOutput.asText.get().trim()

    private fun getProfilerExecutable(): String {
        val isWindows = System.getProperty("os.name").lowercase().contains("windows")
        val executableName = if (isWindows) "gradle-profiler.bat" else "gradle-profiler"
        val executablePath = project.file("gradle/profiler/bin/$executableName")

        if (!executablePath.exists()) {
            throw GradleException("gradle-profiler executable not found at: ${executablePath.absolutePath}")
        }

        return executablePath.absolutePath
    }

    private fun createDynamicScenarios(currentBranch: String, baseBranch: String, config: BranchComparisonExtension): java.io.File {
        val scenariosContent = """
            # Current branch build
            build_current_branch {
                title = "Build Current Branch ($currentBranch)"
                tasks = ${config.targetTasks.get().joinToString(", ") { "\"$it\"" }}
                gradle-args = ${config.gradleArgs.get().joinToString(", ") { "\"$it\"" }}
                cleanup-tasks = ${if (config.cleanBuild.get()) "[\"clean\"]" else "[]"}
            }

            # Base branch build  
            build_base_branch {
                title = "Build Base Branch ($baseBranch)"
                tasks = ${config.targetTasks.get().joinToString(", ") { "\"$it\"" }}
                gradle-args = ${config.gradleArgs.get().joinToString(", ") { "\"$it\"" }}
                cleanup-tasks = ${if (config.cleanBuild.get()) "[\"clean\"]" else "[]"}
                git-checkout = {
                    build = "$baseBranch"
                    cleanup = "$currentBranch"
                }
            }
        """.trimIndent()

        val scenariosFile = project.file("gradle/profiler/branch-comparison.scenarios")
        scenariosFile.writeText(scenariosContent)
        return scenariosFile
    }

    private fun runBenchmark(profilerBin: String, scenariosFile: java.io.File, config: BranchComparisonExtension) {
        val outputDir = config.outputDir.get()
        val iterations = config.iterations.get().toString()
        val warmups = config.warmupRuns.get().toString()

        println("🚀 Starting professional benchmark...")
        println("📈 Iterations: ${config.iterations.get()}")

        val processBuilder = ProcessBuilder(
            profilerBin,
            "--benchmark",
            "--output-dir", outputDir,
            "--scenario-file", scenariosFile.absolutePath,
            "--iterations", iterations,
            "--warmups", warmups,
            "build_current_branch",
            "build_base_branch",
        )

        processBuilder.directory(project.projectDir)

        println("🔧 Command: ${processBuilder.command().joinToString(" ")}")
        println("🔧 Working directory: ${processBuilder.directory()}")

        val process = processBuilder.start()

        // Capture output for better error reporting
        val output = StringBuilder()
        val errorOutput = StringBuilder()

        process.inputStream.bufferedReader().use { reader ->
            reader.lineSequence().forEach { line ->
                println("📤 gradle-profiler: $line")
                output.appendLine(line)
            }
        }

        process.errorStream.bufferedReader().use { reader ->
            reader.lineSequence().forEach { line ->
                System.err.println("⚠️ gradle-profiler error: $line")
                errorOutput.appendLine(line)
            }
        }

        val exitCode = process.waitFor()

        if (exitCode != 0) {
            println("❌ gradle-profiler failed with exit code: $exitCode")
            if (errorOutput.isNotEmpty()) {
                println("Error output:\n$errorOutput")
            }
            throw GradleException("gradle-profiler execution failed with exit code: $exitCode")
        }
    }

    private fun reportResults() {
        val outputDir = extension.get().outputDir.get()

        println("✅ Branch comparison completed!")
        println("📈 HTML Report: $outputDir/benchmark.html")
        println("📋 CSV Data: $outputDir/benchmark.csv")
        println("🎯 Check the results to see performance impact of your changes")

        // Try to extract quick summary from CSV if available
        try {
            val csvFile = project.file("$outputDir/benchmark.csv")
            if (csvFile.exists()) {
                extractQuickSummary(csvFile)
            }
        } catch (e: Exception) {
            // Ignore errors in summary extraction
            println("Could not extract summary: ${e.message}")
        }
    }

    private fun extractQuickSummary(csvFile: java.io.File) {
        val lines = csvFile.readLines()
        if (lines.size > 2) {
            println("📊 Quick Summary:")
            println("   Open $csvFile for detailed results")
            println("   Or view HTML report for visual analysis")
        }
    }

    private fun returnToOriginalBranch(originalBranch: String) {
        try {
            val currentBranch = getCurrentBranch()
            if (currentBranch != originalBranch) {
                println("🔄 Returning to original branch: $originalBranch")
                execOperations.exec {
                    commandLine("git", "checkout", originalBranch)
                }
                println("✅ Successfully returned to branch: $originalBranch")
            }
        } catch (e: Exception) {
            println("⚠️ Warning: Failed to return to original branch '$originalBranch': ${e.message}")
            println("💡 Please manually switch to the desired branch with: git checkout $originalBranch")
        }
    }
}
