import javax.inject.Inject
import org.gradle.api.Project
import org.gradle.api.provider.Property

abstract class BumpVersionExtension @Inject constructor(
    project: Project,
) {
    val isEnabled: Property<Boolean> = project.objects.property(Boolean::class.java)
}
