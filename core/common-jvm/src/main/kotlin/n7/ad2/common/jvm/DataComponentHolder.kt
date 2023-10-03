package n7.ad2.common.jvm

/**
 * Позволяет получить компонент с внешними зависимостями полученные при инициализации
 * Если компонента нет то создает новый
 */
abstract class DataComponentHolder<Component : DIComponent, Data : Any> : BaseComponentHolder<Component> {

    @Volatile
    private var component: Component? = null

    private var data: Data? = null

    override fun get(): Component {
        return component ?: synchronized(this) {
            component ?: build(requireNotNull(data) { "${javaClass.simpleName} - data not found" }).also(::set)
        }
    }

    fun init(data: Data): DataComponentHolder<Component, Data> {
        this.data = data
        return this
    }

    override fun set(component: Component) {
        this.component = component
    }

    protected abstract fun build(data: Data): Component

}
