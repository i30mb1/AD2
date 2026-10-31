package n7.ad2.drawer.internal.domain.usecase

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json
import n7.ad2.Resources
import n7.ad2.app.logger.Logger
import n7.ad2.apppreference.Preference
import n7.ad2.coroutines.DispatchersProvider
import n7.ad2.drawer.internal.data.remote.SettingsApi
import n7.ad2.drawer.internal.data.remote.model.Menu
import n7.ad2.drawer.internal.data.remote.model.Settings
import n7.ad2.drawer.internal.data.remote.model.VOMenuType
import n7.ad2.drawer.internal.domain.vo.VOMenu
import n7.ad2.feature.drawer.R
import javax.inject.Inject

internal class GetMenuItemsUseCase @Inject constructor(
    private val res: Resources,
    private val preference: Preference,
    private val settingsApi: SettingsApi,
    private val logger: Logger,
    private val dispatchers: DispatchersProvider,
) {

    private val defaultMenu = listOf(
        Menu(VOMenuType.HEROES),
        Menu(VOMenuType.ITEMS),
        Menu(VOMenuType.NEWS),
        Menu(VOMenuType.TOURNAMENTS),
        Menu(VOMenuType.STREAMS),
        Menu(VOMenuType.GAMES),
    )

    operator fun invoke(): Flow<List<VOMenu>> = flow {
//        error("use local settings")
        if (preference.isNeedToUpdateSettings()) {
            val newSettings = settingsApi.getSettings()
            preference.saveSettings(Json.encodeToString(newSettings))
            emit(newSettings)
        } else {
            emit(Json.decodeFromString<Settings>(preference.getSettings()) ?: error("could not convert settings from Json"))
        }
    }.catch { error ->
        logger.log("could not load settings (${error.message})")
        emit(Settings(defaultMenu))
    }.map { settings ->
        settings.menu?.map { menu ->
            val (title, subtitle) = when (menu.type) {
                VOMenuType.HEROES -> R.string.heroes to R.string.heroes_subtitle
                VOMenuType.ITEMS -> R.string.items to R.string.items_subtitle
                VOMenuType.NEWS -> R.string.news to R.string.news_subtitle
                VOMenuType.TOURNAMENTS -> R.string.tournaments to R.string.tournaments_subtitle
                VOMenuType.STREAMS -> R.string.streams to R.string.streams_subtitle
                VOMenuType.GAMES -> R.string.games to R.string.games_subtitle
                VOMenuType.UNKNOWN -> return@map VOMenu(menu.type, menu.type.name, R.string.menu_disabled, false)
            }
            VOMenu(
                type = menu.type,
                title = res.getString(title),
                subtitle = if (menu.isEnable) subtitle else R.string.menu_disabled,
                isEnable = menu.isEnable,
                isSelected = menu.type == VOMenuType.HEROES,
            )
        } ?: error("sorry we fucked up...")
    }.flowOn(dispatchers.Default)
}
