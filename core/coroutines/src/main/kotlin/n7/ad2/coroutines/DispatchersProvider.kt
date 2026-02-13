@file:Suppress("PropertyName")

package n7.ad2.coroutines

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

open class DispatchersProvider {
    open val Main: CoroutineDispatcher = Dispatchers.Main
    open val Default: CoroutineDispatcher = Dispatchers.Default // 2 to N(CPU cores) threads for network work
    open val IO: CoroutineDispatcher = Dispatchers.IO // 64 threads for parsing JSON or sorting list
}
