package n7.ad2.di

import com.squareup.moshi.Moshi
import n7.ad2.dagger.ApplicationScope

@dagger.Module
object MoshiModule {

    @ApplicationScope
    @dagger.Provides
    fun moshi(): Moshi = Moshi.Builder().build()

}