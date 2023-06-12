package n7.ad2.common.jvm

interface BaseComponentHolder<Component : DIComponent> {

    fun get(): Component

    fun set(component: Component)

}
