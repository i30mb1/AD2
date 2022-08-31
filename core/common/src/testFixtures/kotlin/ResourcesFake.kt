import n7.ad2.Resources
import java.io.ByteArrayInputStream
import java.io.File
import java.io.InputStream

class ResourcesFake : Resources {
    override fun getString(resourceID: Int): String = ""
    override fun getString(resourceID: Int, formatArgs: Any): String = ""
    override fun getAssets(path: String): InputStream = ByteArrayInputStream(ByteArray(1))
    override fun getExternalFilesDir(path: String): File? = null
    override fun getFilesDir(): File? = null
}