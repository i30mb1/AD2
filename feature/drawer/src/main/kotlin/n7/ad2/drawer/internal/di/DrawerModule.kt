package n7.ad2.drawer.internal.di

import dagger.Lazy
import n7.ad2.drawer.internal.data.MenuInfoSourceImpl
import n7.ad2.drawer.internal.data.remote.SettingsApi
import n7.ad2.drawer.internal.domain.usecase.MenuInfoSource
import okhttp3.OkHttpClient

@dagger.Module
internal object DrawerModule {

    @dagger.Provides
    fun provideSettingsApi(client: Lazy<OkHttpClient>): SettingsApi = SettingsApi.get(client)

    @dagger.Provides
    fun provideMenuInfoSource(impl: MenuInfoSourceImpl): MenuInfoSource = impl
}
