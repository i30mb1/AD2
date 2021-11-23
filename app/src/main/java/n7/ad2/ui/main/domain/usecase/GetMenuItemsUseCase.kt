package n7.ad2.ui.main.domain.usecase

import android.app.Application
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import n7.ad2.R
import n7.ad2.data.source.local.AppPreference
import n7.ad2.data.source.remote.model.VOMenuType
import n7.ad2.data.source.remote.retrofit.SettingsApi
import n7.ad2.ui.main.domain.vo.VOMenu
import javax.inject.Inject

class GetMenuItemsUseCase @Inject constructor(
    private val application: Application,
    private val appPreference: AppPreference,
    private val settingsApi: SettingsApi,
    private val ioDispatcher: CoroutineDispatcher,
) {

    operator fun invoke(): Flow<List<VOMenu>> = flow {
        val settings = settingsApi.getSettings()
        appPreference.saveSettings(settings)
        val settingsFromLocalStore = appPreference.getSettings()
        val result = settingsFromLocalStore.menu.map { menu ->
            when (menu.type) {
                VOMenuType.HEROES -> VOMenu(menu.type, application.getString(R.string.heroes), menu.isEnable, true)
                VOMenuType.ITEMS -> VOMenu(menu.type, application.getString(R.string.items), menu.isEnable)
                VOMenuType.NEWS -> VOMenu(menu.type, application.getString(R.string.news), menu.isEnable)
                VOMenuType.TOURNAMENTS -> VOMenu(menu.type, application.getString(R.string.tournaments), menu.isEnable)
                VOMenuType.STREAMS -> VOMenu(menu.type, application.getString(R.string.streams), menu.isEnable)
                VOMenuType.GAMES -> VOMenu(menu.type, application.getString(R.string.games), menu.isEnable)
                VOMenuType.UNKNOWN -> VOMenu(menu.type, menu.type.name, menu.isEnable)
            }
        }
        emit(result)
    }.flowOn(ioDispatcher)

}