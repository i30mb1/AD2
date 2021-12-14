import org.gradle.api.DefaultTask
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import org.gradle.work.Incremental
import org.gradle.work.InputChanges

@CacheableTask
abstract class FileLinePrefixTask : DefaultTask() {

    @get:InputFile
    @get:Incremental
    abstract val inputFile: RegularFileProperty

    @get:OutputFile
    abstract val outPutFile: RegularFileProperty

    @TaskAction
    open fun prefixFileLine(changes: InputChanges) {
        val output = inputFile.get().asFile.readLines().joinToString("\n") { "prefix: $it" }
        outPutFile.get().asFile.writeText(output)
    }

}