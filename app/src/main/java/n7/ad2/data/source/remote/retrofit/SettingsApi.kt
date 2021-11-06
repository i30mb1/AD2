package n7.ad2.data.source.remote.retrofit

import com.squareup.moshi.Moshi
import n7.ad2.data.source.remote.model.Settings
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.create
import retrofit2.http.GET

interface SettingsApi {

    @GET("settings.json?raw=true")
    suspend fun getSettings(): Settings

    companion object {
        fun get(
            client: OkHttpClient,
            moshi: Moshi,
        ): SettingsApi {
            return Retrofit.Builder()
                .baseUrl("https://github.com/i30mb1/AD2/blob/master/")
                .client(client)
                .addConverterFactory(MoshiConverterFactory.create(moshi))
                .build()
                .create()
        }
    }

}