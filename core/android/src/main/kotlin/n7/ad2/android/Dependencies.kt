@file:Suppress("StaticFieldLeak", "UNCHECKED_CAST")

package n7.ad2.android

import android.app.Activity
import android.app.Application
import androidx.fragment.app.Fragment
import n7.ad2.dagger.Dependencies

typealias DependenciesMap = Map<Class<out Dependencies>, @JvmSuppressWildcards Dependencies>

interface HasDependencies {
    var dependenciesMap: DependenciesMap
}

inline fun <reified D : Dependencies> Fragment.findDependencies(): D {
    return findDependenciesByClass(D::class.java)
}

inline fun <reified D : Dependencies> Activity.findDependencies(): D {
    return findDependenciesByClass(D::class.java)
}

fun <D : Dependencies> Fragment.findDependenciesByClass(clazz: Class<D>): D {
    return parents
        .mapNotNull { it.dependenciesMap[clazz] }
        .firstOrNull() as D?
        ?: throw IllegalStateException("No Dependencies $clazz in ${allParents.joinToString()}")
}

fun <D : Dependencies> Activity.findDependenciesByClass(clazz: Class<D>): D {
    return (application as HasDependencies).dependenciesMap[clazz] as D
}

private val Fragment.parents: Iterable<HasDependencies>
    get() = allParents.mapNotNull { it as? HasDependencies }

private val Fragment.allParents: Iterable<Any>
    get() = object : Iterable<Any> {
        override fun iterator() = object : Iterator<Any> {
            private var currentParentFragment: Fragment? = parentFragment
            private var parentActivity: Activity? = activity
            private var parentApplication: Application? = parentActivity?.application

            override fun hasNext() = currentParentFragment != null || parentActivity != null || parentApplication != null

            override fun next(): Any {
                currentParentFragment?.let { parent ->
                    currentParentFragment = parent.parentFragment
                    return parent
                }

                parentActivity?.let { parent ->
                    parentActivity = null
                    return parent
                }

                parentApplication?.let { parent ->
                    parentApplication = null
                    return parent
                }

                throw NoSuchElementException()
            }
        }
    }