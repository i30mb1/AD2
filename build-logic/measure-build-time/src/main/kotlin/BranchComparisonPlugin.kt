import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.register
import tasks.BranchComparisonTask

/**
 * Plugin for comprehensive branch performance comparison using gradle-profiler.
 *
 * Provides professional benchmarking tools to measure build performance impact
 * of code changes between git branches.
 */
class BranchComparisonPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        with(target) {
            val extension = extensions.create<BranchComparisonExtension>("branchComparison")

            tasks.register<BranchComparisonTask>("benchmarkBranchComparison") {
                group = "profiling"
                description = "Professional branch comparison using gradle-profiler with detailed reports"
                this.extension.set(extension)
            }
        }
    }
}
