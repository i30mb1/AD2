package n7.ad2.common.jvm

abstract class ComponentHolder<Component : DIComponent> : BaseComponentHolder<Component> {

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

class My private constructor(any: String) {

    companion object : SingletonHolder<My, String>(::My)
}

open class SingletonHolder<out T, in A>(private val constructor: (A) -> T) {

    @Volatile
    private var instance: T? = null

    fun getInstance(arg: A): T =
        instance ?: synchronized(this) {
            instance ?: constructor(arg).also { instance = it }
        }
}
