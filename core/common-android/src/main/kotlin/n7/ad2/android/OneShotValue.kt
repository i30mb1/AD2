package n7.ad2.android

import java.util.concurrent.atomic.AtomicReference

/**
 * Класс использующийся для хранения "одноразового" значения во view state. При первом чтении значения, оно выставляется в
 * null, т.е. значение можно считать только один раз. Применяется, например, для чтения ошибок, отображающихся через snack,
 * т.к. при повторном чтении view state (например при configuration change) отображать snack с ошибкой во второй раз не надо
 */
class OneShotValue<T : Any>(value: T) {

    private val value: AtomicReference<T> = AtomicReference(value)

    fun get(): T? = value.getAndSet(null)
}
