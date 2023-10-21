package n7.ad2.heroes.domain.di

import android.app.Application
import com.squareup.moshi.Moshi
import n7.ad2.AppInformation
import n7.ad2.Resources
import n7.ad2.app.logger.Logger
import n7.ad2.coroutines.DispatchersProvider

interface HeroesDomainDependencies {
    val res: Resources
    val moshi: Moshi
    val appInformation: AppInformation
    val dispatcher: DispatchersProvider
    val application: Application
    val logger: Logger
}
