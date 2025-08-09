import javax.inject.Inject
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

abstract class MeasureBuildTask @Inject constructor() : DefaultTask() {

    @TaskAction
    open fun action() {
        project.providers.exec {
            commandLine("gradle/profiler/bin/gradle-profiler.bat")
            args(
                "--benchmark",
                "--output-dir",
                "gradle/profiler/last-output",
                "--scenario-file",
                "gradle/profiler/profiler.scenarios",
                "incremental"
            )
        }
        val output = project.file("gradle-profiler/last-output/benchmark.csv").readLines()
            .filter { it.startsWith("measured") }
            .map { it.substringAfter(",") }
        val average = output.map { it.toInt() }.average().toInt().toString()
        val input = project.file("gradle-profiler/measure.txt")
        if (!input.exists()) input.createNewFile()
        input.appendText("Execution time $average ms\n")
//        project.exec {
//            commandLine("git")
//            args = listOf("config", "user.name", "github-actions")
//        }
//        project.exec {
//            commandLine("git")
//            args = listOf("config", "user.email", "github-actions@github.com")
//        }
//        project.exec {
//            commandLine("git")
//            args = listOf("add", "gradle-profiler")
//        }
//        project.exec {
//            commandLine("git")
//            args = listOf("commit", "-m", "update measure.txt")
//        }
//        project.exec {
//            commandLine("git")
//            args = listOf("push")
//        }
    }

}
