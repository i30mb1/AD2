package n7.ad2.drawer.internal.data.remote

import dagger.Lazy
import kotlinx.serialization.json.Json
import n7.ad2.drawer.internal.data.remote.model.Menu
import n7.ad2.drawer.internal.data.remote.model.Settings
import n7.ad2.drawer.internal.data.remote.model.VOMenuType
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import retrofit2.create
import retrofit2.http.GET

internal interface SettingsApi {

    @GET("settings.json")
    suspend fun getSettings(): Settings

    companion object {
        fun get(
            client: Lazy<OkHttpClient>,
        ): SettingsApi {
            return Retrofit.Builder()
                .baseUrl("https://raw.githubusercontent.com/i30mb1/AD2/master/")
                .callFactory { request -> client.get().newCall(request) }
                .addConverterFactory(Json.asConverterFactory("application/json".toMediaType()))
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