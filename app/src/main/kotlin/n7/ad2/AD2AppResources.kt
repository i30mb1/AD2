package n7.ad2

import android.app.Application
import java.io.File
import java.io.InputStream
import javax.inject.Inject

class AD2AppResources @Inject constructor(
    private val application: Application,
) : AppResources {
    override fun getString(resourceID: Int): String = application.getString(resourceID)
    override fun getString(resourceID: Int, formatArgs: Any): String = application.getString(resourceID, formatArgs)
    override fun getAssets(path: String): InputStream = application.assets.open(path)
    override fun getExternalFilesDir(path: String): File? = application.getExternalFilesDir(path)
}