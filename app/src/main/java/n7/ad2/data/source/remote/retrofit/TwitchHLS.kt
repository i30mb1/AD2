package n7.ad2.data.source.remote.retrofit

import com.squareup.moshi.Moshi
import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.create
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path
import retrofit2.http.Query

interface TwitchHLSApi {

    @Headers(
        "Referer: https://player.twitch.tv",
        "Origin: https://player.twitch.tv",
        "Client-ID: kimne78kx3ncx6brgo4mv6wki5h1ko",
    )
    @GET("{streamerName}.m3u8?")
    suspend fun getUrls(
        @Path("streamerName") streamerName: String,
        @Query("player") player: String = "twitchweb",
        @Query("allow_audio_only") allow_audio_only: Boolean = true,
        @Query("allow_source") allow_source: Boolean = true,
        @Query("type") type: String = "any",
        @Query("p") p: Int,
        @Query("sig") sig: String,
        @Query("token") token: String,
    ): Response<Unit>

    companion object {

        fun get(client: OkHttpClient, moshi: Moshi): TwitchHLSApi {
            return Retrofit.Builder()
                .baseUrl("http://usher.twitch.tv/api/channel/hls/")
                .client(OkHttpClient())
                .addConverterFactory(MoshiConverterFactory.create(moshi))
                .build()
                .create()
        }

    }

}