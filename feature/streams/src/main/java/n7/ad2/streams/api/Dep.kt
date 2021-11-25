package n7.ad2.streams.api

import ad2.n7.android.Dependencies
import ad2.n7.android.Naruto
import kotlin.reflect.KClass

@dagger.MapKey
annotation class DependenciesKey(val value: KClass<out Dependencies>)
