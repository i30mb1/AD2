import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

abstract class MeasureBuildTask : DefaultTask() {

    @TaskAction
    open fun action() {
        val output = project.file("gradle-profiler/last-output/benchmark.csv").readLines()
            .filter { it.startsWith("measured") }
            .map { it.substringAfter(",") }
        val average = output.map { it.toInt() }.average().toInt().toString()
        val input = project.file("gradle-profiler/measure.txt")
        if (!input.exists()) input.createNewFile()
        input.appendText("Execution time $average ms\n")
    }

}