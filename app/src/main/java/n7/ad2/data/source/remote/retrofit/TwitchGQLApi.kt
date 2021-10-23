package n7.ad2.data.source.remote.retrofit

import com.squareup.moshi.Moshi
import n7.ad2.data.source.remote.model.StreamGQL
import n7.ad2.data.source.remote.model.StreamGQLRequest
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.create
import retrofit2.http.Body
import retrofit2.http.POST

interface TwitchGQLApi {

    @POST("gql")
    suspend fun getStreamGQL(
        @Body body: StreamGQLRequest,
    ): StreamGQL

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