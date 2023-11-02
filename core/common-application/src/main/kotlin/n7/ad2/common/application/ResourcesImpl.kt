package n7.ad2.common.application

import android.content.Context
import java.io.File
import java.io.InputStream
import n7.ad2.Resources
import n7.ad2.common.jvm.ComponentHolder

private class ResourcesImpl(
    private val context: Context,
) : Resources {
    override fun getString(resourceID: Int): String = context.getString(resourceID)
    override fun getString(resourceID: Int, formatArgs: Any): String = context.getString(resourceID, formatArgs)
    override fun getAssets(path: String): InputStream = context.assets.open(path)
    override fun getExternalFilesDir(path: String): File? = context.getExternalFilesDir(path)
    override fun getFilesDir(): File? = context.noBackupFilesDir
    override fun getConfiguration(): Any? = context.resources.configuration
}

fun Resources(context: Context): Resources {
    return ResourcesImpl(context)
}

object ResourcesHolder : ComponentHolder<Resources>() {
    override fun build(): Resources = TODO()
}
