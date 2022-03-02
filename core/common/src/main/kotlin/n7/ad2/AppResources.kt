package n7.ad2

import java.io.File
import java.io.InputStream

interface AppResources {
    fun getString(resourceID: Int): String
    fun getString(resourceID: Int, formatArgs: Any): String
    fun getAssets(path: String): InputStream
    fun getExternalFilesDir(path: String): File?
}