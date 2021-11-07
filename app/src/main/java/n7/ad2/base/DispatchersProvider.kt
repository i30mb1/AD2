@file:Suppress("PropertyName")

package n7.ad2.base

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

open class DispatchersProvider {

    open val Default: CoroutineDispatcher = Dispatchers.Default
    open val Main: CoroutineDispatcher = Dispatchers.Main
    open val IO: CoroutineDispatcher = Dispatchers.IO

}