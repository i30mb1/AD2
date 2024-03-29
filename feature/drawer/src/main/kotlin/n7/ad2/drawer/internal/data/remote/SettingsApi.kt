package n7.ad2.drawer.internal.data.remote

import com.squareup.moshi.Moshi
import dagger.Lazy
import n7.ad2.drawer.internal.data.remote.model.Menu
import n7.ad2.drawer.internal.data.remote.model.Settings
import n7.ad2.drawer.internal.data.remote.model.VOMenuType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.create
import retrofit2.http.GET

internal interface SettingsApi {

    @GET("settings.json")
    suspend fun getSettings(): Settings

    companion object {
        fun get(
            client: Lazy<OkHttpClient>,
            moshi: Moshi,
        ): SettingsApi {
            return Retrofit.Builder()
                .baseUrl("https://raw.githubusercontent.com/i30mb1/AD2/master/")
                .callFactory { request -> client.get().newCall(request) }
                .addConverterFactory(MoshiConverterFactory.create(moshi))
                .build()
                .create()
        }
    }

}

internal class SettingsApiFake : SettingsApi {
    var isError = false
    override suspend fun getSettings(): Settings {
        return if (isError) {
            error("oops!")
        } else {
            Settings(
                menu = listOf(
                    Menu(VOMenuType.HEROES, true),
                    Menu(VOMenuType.NEWS, false),
                )
            )
        }
    }
}