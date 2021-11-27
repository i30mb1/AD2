package ad2.n7.dagger

import ad2.n7.android.Dependencies
import kotlin.reflect.KClass

@dagger.MapKey
annotation class DependenciesKey(val value: KClass<out Dependencies>)