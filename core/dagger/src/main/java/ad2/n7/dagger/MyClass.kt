package ad2.n7.dagger

import kotlin.reflect.KClass

@dagger.MapKey
annotation class DependenciesKey(val value: KClass<out Dependencies>)