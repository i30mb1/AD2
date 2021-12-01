package n7.ad2.android.extension

inline fun <T> lazyUnsafe(
    crossinline initializer: () -> T,
) = lazy(mode = LazyThreadSafetyMode.NONE) {
    initializer.invoke()
}