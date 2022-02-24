package n7.ad2.drawer.internal.domain.usecase

import android.app.Application
import com.squareup.moshi.Moshi
import com.squareup.moshi.adapter
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import n7.ad2.app_preference.AppPreference
import n7.ad2.coroutines.DispatchersProvider
import n7.ad2.drawer.R
import n7.ad2.drawer.internal.data.remote.SettingsApi
import n7.ad2.drawer.internal.data.remote.model.Settings
import n7.ad2.drawer.internal.data.remote.model.VOMenuType
import n7.ad2.drawer.internal.domain.vo.VOMenu
import n7.ad2.logger.AD2Logger
import javax.inject.Inject

internal class GetMenuItemsUseCase @Inject constructor(
    private val application: Application,
    private val appPreference: AppPreference,
    private val settingsApi: SettingsApi,
    private val logger: AD2Logger,
    private val moshi: Moshi,
    private val dispatchers: DispatchersProvider,
) {

    @OptIn(ExperimentalStdlibApi::class)
    private val settingsAdapter = moshi.adapter<Settings>()

    operator fun invoke(): Flow<List<VOMenu>> = flow {
        if (appPreference.isNeedToUpdateSettings()) {
            val newSettings = settingsApi.getSettings()
            appPreference.saveSettings(settingsAdapter.toJson(newSettings))
            emit(newSettings)
        } else {
            emit(settingsAdapter.fromJson(appPreference.getSettings()) ?: Settings())
        }
    }
        .catch { error ->
            logger.log("Could not load settings (${error.message})")
            emit(settingsAdapter.fromJson(appPreference.getSettings()) ?: Settings())
        }
        .map { settings ->
            settings.menu.map { menu ->
                when (menu.type) {
                    VOMenuType.HEROES -> VOMenu(menu.type, application.getString(R.string.heroes), menu.isEnable, true)
                    VOMenuType.ITEMS -> VOMenu(menu.type, application.getString(R.string.items), menu.isEnable)
                    VOMenuType.NEWS -> VOMenu(menu.type, application.getString(R.string.news), menu.isEnable)
                    VOMenuType.TOURNAMENTS -> VOMenu(menu.type, application.getString(R.string.tournaments), menu.isEnable)
                    VOMenuType.STREAMS -> VOMenu(menu.type, application.getString(R.string.streams), menu.isEnable)
                    VOMenuType.GAMES -> VOMenu(menu.type, application.getString(R.string.games), menu.isEnable)
                    VOMenuType.UNKNOWN -> VOMenu(menu.type, menu.type.name, false)
                }
            }
        }
        .flowOn(dispatchers.Default)

}