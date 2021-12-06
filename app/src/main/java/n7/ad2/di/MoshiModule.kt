package n7.ad2.di

import com.squareup.moshi.Moshi
import dagger.Provides
import javax.inject.Singleton

@dagger.Module
class MoshiModule {

    @Singleton
    @Provides
    fun moshi(): Moshi = Moshi.Builder().build()

}