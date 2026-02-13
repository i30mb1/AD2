package n7.ad2.common.jvm

interface BaseComponentHolder<Component> {

    fun get(): Component

    fun set(component: Component)
}
