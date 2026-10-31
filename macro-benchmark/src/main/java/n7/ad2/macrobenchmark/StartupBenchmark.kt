package n7.ad2.macrobenchmark

import androidx.benchmark.ExperimentalBenchmarkConfigApi
import androidx.benchmark.ExperimentalConfig
import androidx.benchmark.StartupInsightsConfig
import androidx.benchmark.macro.CompilationMode
import androidx.benchmark.perfetto.ExperimentalPerfettoCaptureApi
import androidx.benchmark.macro.StartupMode
import androidx.benchmark.macro.StartupTimingMetric
import androidx.benchmark.macro.junit4.MacrobenchmarkRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Cold start of the `benchmark` build of `:app` with App Startup Insights:
 * after the metrics the report lists startup problems found in the trace
 * (main thread binder calls, lock contention, JIT, CPU contention, ...).
 *
 * `startupCompilationNone` - worst case (no AOT, no baseline profile),
 * `startupBaselineProfile` - what users get after install from Play.
 */
@OptIn(ExperimentalBenchmarkConfigApi::class, ExperimentalPerfettoCaptureApi::class)
@RunWith(AndroidJUnit4::class)
class StartupBenchmark {

    @get:Rule
    val benchmarkRule = MacrobenchmarkRule()

    @Test
    fun startupCompilationNone() = startup(CompilationMode.None())

    @Test
    fun startupBaselineProfile() = startup(CompilationMode.Partial())

    private fun startup(compilationMode: CompilationMode) = benchmarkRule.measureRepeated(
        packageName = "n7.ad2",
        metrics = listOf(StartupTimingMetric()),
        iterations = 10,
        experimentalConfig = ExperimentalConfig(startupInsightsConfig = StartupInsightsConfig(isEnabled = true)),
        compilationMode = compilationMode,
        startupMode = StartupMode.COLD,
        setupBlock = { pressHome() },
    ) {
        startActivityAndWait()
    }
}
