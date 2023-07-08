package n7.ad2.common.application

import android.app.Application
import n7.ad2.Resources
import java.io.File
import java.io.InputStream

private class AD2Resources(
    private val application: Application,
) : Resources {
    override fun getString(resourceID: Int): String = application.getString(resourceID)
    override fun getString(resourceID: Int, formatArgs: Any): String = application.getString(resourceID, formatArgs)
    override fun getAssets(path: String): InputStream = application.assets.open(path)
    override fun getExternalFilesDir(path: String): File? = application.getExternalFilesDir(path)
    override fun getFilesDir(): File? = application.noBackupFilesDir
    override fun getConfiguration(): Any? = application.resources.configuration
}

fun resourcesFactory(application: Application): Resources {
    return AD2Resources(application)
}
