package n7.ad2.common.jvm

abstract class ComponentHolder<Component: DIComponent>: BaseComponentHolder<Component> {

    @Volatile
    private var component: Component? = null

    override fun get(): Component {
        return component ?: synchronized(this) {
            component ?: build().also(::set)
        }
    }

    override fun set(component: Component) {
        this.component = component
    }

    protected abstract fun build(): Component

}
