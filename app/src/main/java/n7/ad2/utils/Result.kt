package n7.ad2.utils

sealed class Result<out T> {
    class Failure<out T>(val exception: Throwable) : Result<T>()
    class Success<out T>(val value: T) : Result<T>()

    companion object {
       fun <T> success(value: T): Success<T> = Success(value)
        @JvmStatic
       fun <T> failure(exception: Throwable): Failure<T> = Failure(exception)
    }

}

inline fun <T> Result<T>.onSuccess(action: (value: T) -> Unit): Result<T> {
    (this as? Result.Success)?.let { action(it.value) }
    return this
}

inline fun <T> Result<T>.onFailure(action: (exception: Throwable) -> Unit): Result<T> {
    (this as? Result.Failure)?.let { action(it.exception) }
    return this
}