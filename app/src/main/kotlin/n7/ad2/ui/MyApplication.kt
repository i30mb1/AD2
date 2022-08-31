package n7.ad2.ui

import android.app.Application
import n7.ad2.AppInformation
import n7.ad2.android.DependenciesMap
import n7.ad2.android.HasDependencies
import n7.ad2.di.ApplicationComponent
import n7.ad2.di.DaggerApplicationComponent
import n7.ad2.di.DaggerComponentProvider
import n7.ad2.init.Initializer
import n7.ad2.ktx.lazyUnsafe
import n7.ad2.logger.Logger
import javax.inject.Inject

// https://medium.com/bumble-tech/how-we-achieved-a-6x-reduction-of-anrs-part-2-fixing-anrs-24fedf9a973f
// “Code never lies, comments sometimes do” — Ron Jeffries
class MyApplication : Application(), DaggerComponentProvider, HasDependencies {

    @Inject override lateinit var dependenciesMap: DependenciesMap

    override val component: ApplicationComponent by lazyUnsafe { DaggerApplicationComponent.factory().create(this) }

    override fun onCreate() {
        component.inject(this)
        super.onCreate()
    }

    @Inject
    fun init(initializers: Set<@JvmSuppressWildcards Initializer>, logger: Logger, appInformation: AppInformation) {
        initializers.forEach { initializer -> initializer.init(this, logger, appInformation) }
    }

}