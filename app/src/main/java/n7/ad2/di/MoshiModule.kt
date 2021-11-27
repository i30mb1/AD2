package n7.ad2.di

import com.squareup.moshi.Moshi
import dagger.Provides
import n7.ad2.data.source.remote.adapter.StringVOMenuTypeAdapter
import javax.inject.Singleton

@dagger.Module
class MoshiModule {

    @Singleton
    @Provides
    fun moshi(): Moshi = Moshi.Builder()
        .add(StringVOMenuTypeAdapter())
        .build()

}