import org.gradle.api.DefaultTask
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.api.tasks.TaskAction
import org.gradle.work.Incremental
import org.gradle.work.InputChanges
import java.io.InputStreamReader
import java.util.Properties

@CacheableTask
abstract class BumpVersionTask : DefaultTask() {

    @get:InputFile
    @get:Incremental
    @get:PathSensitive(PathSensitivity.NAME_ONLY)
    abstract val inputFile: RegularFileProperty

    @get:OutputFile
    abstract val outPutFile: RegularFileProperty

    @TaskAction
    open fun prefixFileLine(changes: InputChanges) {
        val properties = Properties()
        inputFile.get().asFile.inputStream().use { reader ->
            properties.load(reader)
        }

        var patch = properties.getProperty("PATCH").toInt()
        patch++
        properties.setProperty("PATCH", patch.toString())

        outPutFile.get().asFile.outputStream().use { output ->
            properties.store(output, null)
        }
    }

}