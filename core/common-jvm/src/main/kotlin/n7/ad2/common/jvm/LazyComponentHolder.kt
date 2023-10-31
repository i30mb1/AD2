package n7.ad2.common.jvm

abstract class LazyComponentHolder<Component> : BaseComponentHolder<Component> {

    @Volatile
    private var component: Component? = null
    private var provider: () -> Component = { error("${javaClass.simpleName} - component provider not found") }

    override fun get(): Component {
        return component ?: synchronized(this) {
            component ?: provider().also { set(it) }
        }
    }

    /**
     * Для тестов
     */
    override fun set(component: Component) {
        this.component = component
    }

    fun setProvider(provider: () -> Component) {
        this.provider = provider
    }
}
