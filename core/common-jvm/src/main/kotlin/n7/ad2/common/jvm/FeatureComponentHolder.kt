package n7.ad2.common.jvm

import java.lang.ref.WeakReference

/**
 * Холдер компонента с автоматической очисткой
 * Позволяет получить компонент, а если его нет со создает новый
 * Может очиститься при отсуствии ссылок на компонент, не гарантировано существование синглтонов!
 *
 * !!!ВАЖНО
 * Не использовать этот холдер если в вашем компоненте имеется какой либо скоуп (@Singleton, свой кастомный и др.)
 * Так как он может очистится при отсутсвии ссылок на компонент
 */
abstract class FeatureComponentHolder<Component : DIComponent> : BaseComponentHolder<Component> {

    @Volatile
    private var component: WeakReference<Component>? = null

    override fun get(): Component {
        return component?.get() ?: synchronized(this) {
            component?.get() ?: build().also(::set)
        }
    }

    /**
     * Метод может использоваться только в тестах для подмены на тестовые сущности
     */
    override fun set(component: Component) {
        this.component = WeakReference(component)
    }

    protected abstract fun build(): Component

}
