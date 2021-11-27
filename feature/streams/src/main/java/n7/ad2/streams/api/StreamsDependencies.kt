package n7.ad2.streams.api

import ad2.n7.android.Dependencies
import com.squareup.moshi.Moshi
import okhttp3.OkHttpClient

interface StreamsDependencies : Dependencies {
    val client: OkHttpClient
    val moshi: Moshi
}