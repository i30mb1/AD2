package n7.ad2.drawer.internal.domain.usecase

import com.squareup.moshi.Moshi
import com.squareup.moshi.adapter
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import n7.ad2.Resources
import n7.ad2.apppreference.Preference
import n7.ad2.coroutines.DispatchersProvider
import n7.ad2.feature.drawer.R
import n7.ad2.drawer.internal.data.remote.SettingsApi
import n7.ad2.drawer.internal.data.remote.model.Menu
import n7.ad2.drawer.internal.data.remote.model.Settings
import n7.ad2.drawer.internal.data.remote.model.VOMenuType
import n7.ad2.drawer.internal.domain.vo.VOMenu
import n7.ad2.ktx.lazyUnsafe
import n7.ad2.app.logger.Logger
import javax.inject.Inject

internal class GetMenuItemsUseCase @Inject constructor(
    private val res: Resources,
    private val preference: Preference,
    private val settingsApi: SettingsApi,
    private val logger: Logger,
    moshi: Moshi,
    private val dispatchers: DispatchersProvider,
) {

    private val defaultMenu = listOf(
        Menu(VOMenuType.HEROES),
        Menu(VOMenuType.ITEMS),
        Menu(VOMenuType.NEWS),
        Menu(VOMenuType.TOURNAMENTS, false),
        Menu(VOMenuType.STREAMS, false),
        Menu(VOMenuType.GAMES, false),
    )

    @OptIn(ExperimentalStdlibApi::class)
    private val settingsAdapter by lazyUnsafe { moshi.adapter<Settings>() }

    operator fun invoke(): Flow<List<VOMenu>> = flow {
        error("use local settings")
        if (preference.isNeedToUpdateSettings()) {
            val newSettings = settingsApi.getSettings()
            preference.saveSettings(settingsAdapter.toJson(newSettings))
            emit(newSettings)
        } else {
            emit(settingsAdapter.fromJson(preference.getSettings()) ?: error("could not convert settings from Json"))
        }
    }.catch { error ->
        logger.log("could not load settings (${error.message})")
        emit(Settings(defaultMenu))
    }.map { settings ->
        settings.menu?.mapNotNull { menu ->
            when (menu.type) {
                VOMenuType.HEROES -> VOMenu(menu.type, res.getString(R.string.heroes), menu.isEnable, true)
                VOMenuType.ITEMS -> VOMenu(menu.type, res.getString(R.string.items), menu.isEnable)
                VOMenuType.NEWS -> VOMenu(menu.type, res.getString(R.string.news), menu.isEnable)
                VOMenuType.TOURNAMENTS -> VOMenu(menu.type, res.getString(R.string.tournaments), menu.isEnable)
                VOMenuType.STREAMS -> VOMenu(menu.type, res.getString(R.string.streams), menu.isEnable)
                VOMenuType.GAMES -> VOMenu(menu.type, res.getString(R.string.games), menu.isEnable)
                VOMenuType.UNKNOWN -> VOMenu(menu.type, menu.type.name, false)
                else -> null
            }
        } ?: error("sorry we fucked up...")
    }.flowOn(dispatchers.Default)

}