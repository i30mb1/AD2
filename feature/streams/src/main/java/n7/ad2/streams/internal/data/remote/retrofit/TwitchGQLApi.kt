package n7.ad2.streams.internal.data.remote.retrofit

import com.squareup.moshi.Moshi
import n7.ad2.streams.internal.data.remote.model.StreamGQLData
import n7.ad2.streams.internal.data.remote.model.StreamGQLRequest
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.create
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface TwitchGQLApi {

    @Headers("Client-ID: kimne78kx3ncx6brgo4mv6wki5h1ko")
    @POST("gql/")
    suspend fun getStreamGQL(
        @Body body: StreamGQLRequest,
    ): StreamGQLData

    companion object {

        fun get(
            client: OkHttpClient,
            moshi: Moshi,
        ): TwitchGQLApi {
            return Retrofit.Builder()
                .baseUrl("https://gql.twitch.tv/")
                .client(client)
                .addConverterFactory(MoshiConverterFactory.create(moshi))
                .build()
                .create()
        }

    }

}