package n7.ad2.common.jvm

/**
 * Позволяет получить компонент
 * Если компонента нет то создает новый
 */
abstract class ComponentHolder<Component> : BaseComponentHolder<Component> {

    @Volatile
    private var component: Component? = null

    override fun get(): Component = component ?: synchronized(this) {
        component ?: build().also(::set)
    }

    override fun set(component: Component) {
        this.component = component
    }

    protected abstract fun build(): Component
}

open class SingletonHolder<out T, in A>(private val constructor: (A) -> T) {

    @Volatile
    private var instance: T? = null

    fun getInstance(arg: A): T = instance ?: synchronized(this) {
        instance ?: constructor(arg).also { instance = it }
    }
}
