package n7.ad2.streams.api

import com.squareup.moshi.Moshi
import n7.ad2.android.Dependencies
import okhttp3.OkHttpClient

interface StreamsDependencies : Dependencies {
    val client: OkHttpClient
    val moshi: Moshi
}