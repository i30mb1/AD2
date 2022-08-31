package n7.ad2

import java.io.ByteArrayInputStream
import java.io.File
import java.io.InputStream

interface Resources {
    fun getString(resourceID: Int): String
    fun getString(resourceID: Int, formatArgs: Any): String
    fun getAssets(path: String): InputStream
    fun getExternalFilesDir(path: String): File?
    fun getFilesDir(): File?
}

class ResourcesFake : Resources {
    override fun getString(resourceID: Int): String = ""
    override fun getString(resourceID: Int, formatArgs: Any): String = ""
    override fun getAssets(path: String): InputStream = ByteArrayInputStream(ByteArray(1))
    override fun getExternalFilesDir(path: String): File? = null
    override fun getFilesDir(): File? = null
}