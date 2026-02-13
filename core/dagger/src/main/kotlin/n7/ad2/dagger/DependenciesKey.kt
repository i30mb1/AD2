package n7.ad2.dagger

import kotlin.reflect.KClass

interface Dependencies

@dagger.MapKey
annotation class DependenciesKey(val value: KClass<out Dependencies>)
