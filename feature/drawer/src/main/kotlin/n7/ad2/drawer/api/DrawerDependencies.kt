package n7.ad2.drawer.api

import android.app.Application
import n7.ad2.Resources
import n7.ad2.app.logger.Logger
import n7.ad2.apppreference.Preference
import n7.ad2.coroutines.DispatchersProvider
import n7.ad2.dagger.Dependencies
import n7.ad2.heroes.domain.usecase.GetHeroesUseCase
import n7.ad2.items.domain.usecase.GetItemsUseCase
import n7.ad2.items.domain.usecase.GetNewsUseCase
import n7.ad2.navigator.Navigator
import okhttp3.OkHttpClient

interface DrawerDependencies : Dependencies {
    val application: Application
    val res: Resources
    val preference: Preference
    val navigator: Navigator
    val logger: Logger
    val okHttpClient: OkHttpClient
    val dispatchersProvider: DispatchersProvider
    val getHeroesUseCase: GetHeroesUseCase
    val getItemsUseCase: GetItemsUseCase
    val getNewsUseCase: GetNewsUseCase
}
