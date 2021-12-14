import org.gradle.api.DefaultTask
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction

abstract class FileLinePrefixTask : DefaultTask() {

    @get:Internal abstract val inputFile: RegularFileProperty
    @get:Internal abstract val outPutFile: RegularFileProperty

    @TaskAction
    open fun prefixFileLine() {
        val output = inputFile.get().asFile.readLines().joinToString("\n") { "prefix: $it" }
        outPutFile.get().asFile.writeText(output)
    }

}