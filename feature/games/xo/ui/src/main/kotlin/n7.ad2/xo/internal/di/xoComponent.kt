package n7.ad2.xo.internal.di

import n7.ad2.xo.api.XoDependencies
import n7.ad2.xo.internal.XoFragment
import n7.ad2.xo.internal.XoViewModel

@dagger.Component(
    dependencies = [
        XoDependencies::class
    ],
)
internal interface XoComponent {

    @dagger.Component.Factory
    interface Factory {
        fun create(dependencies: XoDependencies): XoComponent
    }

    fun inject(xoFragment: XoFragment)

    val xoViewModelFactory: XoViewModel.Factory
}
