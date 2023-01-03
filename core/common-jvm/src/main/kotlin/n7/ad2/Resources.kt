package n7.ad2

import java.io.File
import java.io.InputStream

interface Resources {
    fun getString(resourceID: Int): String
    fun getString(resourceID: Int, formatArgs: Any): String
    fun getAssets(path: String): InputStream
    fun getExternalFilesDir(path: String): File?
    fun getFilesDir(): File?
    fun getConfiguration(): Any?
}