package n7.ad2.di

import com.squareup.moshi.Moshi
import dagger.Provides
import n7.ad2.dagger.ApplicationScope

@dagger.Module
class MoshiModule {

    @ApplicationScope
    @Provides
    fun moshi(): Moshi = Moshi.Builder().build()

}