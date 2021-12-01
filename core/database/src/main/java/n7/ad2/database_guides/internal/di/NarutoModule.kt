package n7.ad2.database_guides.internal.di

import dagger.Module
import dagger.Provides

@Module
class NarutoModule {

    @Provides
    fun provideNaruto(): Naruto = Naruto()

}

class Naruto