package n7.ad2.utils

sealed class Result<out T : Any> {
    class Failure<out T : Any>(val exception: Throwable) : Result<T>()
    class Success<out T : Any>(val value: T) : Result<T>()

    companion object {
        fun <T : Any> success(value: T): Success<T> = Success(value)
        fun failure(exception: Throwable): Failure<Nothing> = Failure(exception)
    }

}

inline fun <T : Any> Result<T>.onSuccess(action: (value: T) -> Unit): Result<T> {
    (this as? Result.Success)?.let { action(it.value) }
    return this
}

inline fun <T : Any> Result<T>.onFailure(action: (exception: Throwable) -> Unit): Result<T> {
    (this as? Result.Failure)?.let { action(it.exception) }
    return this
}