package n7.ad2.di

import dagger.Provides
import n7.ad2.provider.AD2Provider
import n7.ad2.provider.Provider

@dagger.Module
class NewApplicationModule {

    @Provides
    fun provideProvider(): Provider = AD2Provider

}