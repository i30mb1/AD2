package n7.ad2

import android.app.Application
import java.io.InputStream
import javax.inject.Inject

class AD2AppResources @Inject constructor(
    private val application: Application,
) : AppResources {
    override fun getString(resourceID: Int): String = application.getString(resourceID)
    override fun getAssets(fileName: String): InputStream = application.assets.open(fileName)
}