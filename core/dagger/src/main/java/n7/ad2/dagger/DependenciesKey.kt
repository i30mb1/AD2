package n7.ad2.dagger

import n7.ad2.android.Dependencies
import kotlin.reflect.KClass

@dagger.MapKey
annotation class DependenciesKey(val value: KClass<out Dependencies>)