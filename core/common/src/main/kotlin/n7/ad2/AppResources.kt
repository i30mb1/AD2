package n7.ad2

import java.io.InputStream

interface AppResources {
    fun getString(resourceID: Int): String
    fun getAssets(fileName: String): InputStream
}