import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * Main measure build time plugin.
 *
 * Provides comprehensive build performance measurement tools.
 */
class MeasureBuildPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        // Apply branch comparison functionality
        target.pluginManager.apply(BranchComparisonPlugin::class.java)
    }
}
