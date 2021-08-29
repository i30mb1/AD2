package n7.ad2.utils

sealed class ResultState<out T : Any> {
    class Failure(val exception: Throwable) : ResultState<Nothing>()
    class Success<out T : Any>(val value: T) : ResultState<T>()

    companion object {
        fun <T : Any> success(value: T): Success<T> = Success(value)
        fun failure(exception: Throwable): Failure = Failure(exception)
    }

}

inline fun <T : Any> ResultState<T>.onSuccess(action: (value: T) -> Unit): ResultState<T> {
    (this as? ResultState.Success)?.let { action(it.value) }
    return this
}

inline fun <T : Any> ResultState<T>.onFailure(action: (exception: Throwable) -> Unit): ResultState<T> {
    (this as? ResultState.Failure)?.let { action(it.exception) }
    return this
}