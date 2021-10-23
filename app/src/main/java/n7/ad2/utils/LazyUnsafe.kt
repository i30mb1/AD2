package n7.ad2.utils

inline fun <T> lazyUnsafe(
    crossinline initializer: () -> T,
) = lazy(mode = LazyThreadSafetyMode.NONE) {
    initializer.invoke()
}