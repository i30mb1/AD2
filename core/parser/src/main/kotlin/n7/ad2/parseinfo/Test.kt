@file:OptIn(ExperimentalTime::class)

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.squareup.moshi.Moshi
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.create
import retrofit2.http.GET
import retrofit2.http.Query
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.time.ExperimentalTime


@JsonClass(generateAdapter = true)
data class Info(
    @Json(name = "age") val age: Int = 0,
    @Json(name = "count") val count: Int = 0,
    @Json(name = "name") val name: String = "",
)

interface MyApi {
    @GET(".")
    suspend fun getInfo(@Query("name") name: String): Info

    companion object {
        fun get(): MyApi {
            return Retrofit.Builder().baseUrl("https://api.agify.io/4234/").addConverterFactory(MoshiConverterFactory.create(Moshi.Builder().build())).build().create()
        }
    }
}

val crew = flowOf("Luffy", "Chopper", "Zoro", "Nami")
val scope = CoroutineScope(Job())

sealed class State(open val adID: String) {
    data class Loading(override val adID: String) : State(adID)
}

private var i = 0
suspend fun main() {
    coroutineScope {
        repeat(100_000) {
            increase()
        }
    }
    println(i)
//    delay(10.seconds)
}

fun increase() = scope.launch {
    synchronized(this) {
        i++
    }
}

fun onError(throwable: Throwable) = scope.launch {
    println(throwable.stackTraceToString())
}

inline fun CoroutineScope.launchSave(
    context: CoroutineContext = EmptyCoroutineContext,
    crossinline onError: (Throwable) -> Unit,
    crossinline block: suspend () -> Unit,
): Job {
    val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        onError(Exception(throwable))
    }
    return launch(exceptionHandler + context) { block() }
}