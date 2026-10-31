package n7.ad2.streams.internal.data.remote.retrofit

import dagger.Lazy
import kotlinx.serialization.json.Json
import n7.ad2.streams.internal.data.remote.Streams
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import retrofit2.create
import retrofit2.http.GET
import retrofit2.http.Query

internal interface TwitchApi {

    @GET("streams?game_id=29595")
    suspend fun getStreams(
        @Query("first") first: Int = 100,
        @Query("after") after: String = "",
        @Query("before") before: String = "",
        // null leaves the parameter out of the request: streams in every language
        @Query("language") language: String? = null,
    ): Streams

    /** Empty list when the channel is offline. */
    @GET("streams")
    suspend fun getStream(@Query("user_login") login: String): Streams

    companion object {

        fun get(client: Lazy<OkHttpClient>): TwitchApi = Retrofit.Builder()
            .baseUrl("https://api.twitch.tv/helix/")
            .callFactory { request -> client.get().newCall(request) }
            .addConverterFactory(Json { ignoreUnknownKeys = true }.asConverterFactory("application/json".toMediaType()))
            .build()
            .create()
    }
}
