package n7.ad2.hero_page.api

import android.app.Application
import androidx.work.WorkManager
import com.squareup.moshi.Moshi
import n7.ad2.AppInformation
import n7.ad2.Resources
import n7.ad2.coroutines.DispatchersProvider
import n7.ad2.dagger.Dependencies
import n7.ad2.logger.Logger
import n7.ad2.provider.Provider
import n7.ad2.repositories.HeroRepository

interface HeroPageDependencies : Dependencies {
    val application: Application
    val provider: Provider
    val workManager: WorkManager
    val heroRepository: HeroRepository
    val logger: Logger
    val moshi: Moshi
    val dispatchersProvider: DispatchersProvider
    val appInformation: AppInformation
    val resources: Resources
}