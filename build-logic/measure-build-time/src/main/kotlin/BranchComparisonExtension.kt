import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property

/**
 * Configuration extension for BranchComparisonPlugin.
 *
 * Allows customization of benchmark behavior:
 * ```
 * branchComparison {
 *     baseBranch.set("develop")
 *     iterations.set(5)
 *     targetTasks.set(listOf(":app:assembleDebug", ":app:assembleRelease"))
 *     outputDir.set("custom-benchmark-output")
 * }
 * ```
 */
abstract class BranchComparisonExtension {

    /**
     * Base branch to compare against (default: "master")
     */
    abstract val baseBranch: Property<String>

    /**
     * Number of benchmark iterations (default: 3 for quick, 10 for detailed)
     */
    abstract val iterations: Property<Int>

    /**
     * Tasks to benchmark (default: [":app:assembleDebug"])
     */
    abstract val targetTasks: ListProperty<String>

    /**
     * Output directory for benchmark results (default: "gradle/profiler/last-output")
     */
    abstract val outputDir: Property<String>

    /**
     * Number of warm-up runs
     */
    abstract val warmupRuns: Property<Int>

    /**
     * Whether to clean build directory before each run (default: true)
     */
    abstract val cleanBuild: Property<Boolean>

    /**
     * Additional gradle arguments for benchmark runs
     */
    abstract val gradleArgs: ListProperty<String>

    init {
        // Set defaults
        baseBranch.convention("master")
        iterations.convention(6)
        targetTasks.convention(listOf(":app:assembleDebug"))
        outputDir.convention("gradle/profiler/last-output")
        warmupRuns.convention(3)
        cleanBuild.convention(true)
        gradleArgs.convention(listOf("--no-build-cache"))
    }
}
