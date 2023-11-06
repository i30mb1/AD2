package n7.ad2.xo.internal

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import javax.inject.Inject
import n7.ad2.android.DependenciesMap
import n7.ad2.android.HasDependencies
import n7.ad2.android.findDependencies
import n7.ad2.app.logger.Logger
import n7.ad2.xo.internal.di.DaggerXoComponent
import n7.ad2.ktx.viewModel
import n7.ad2.ui.ComposeView

internal class XoFragment(
    override var dependenciesMap: DependenciesMap,
) : Fragment(), HasDependencies {

    @Inject lateinit var xoViewModelFactory: XoViewModel.Factory
    @Inject lateinit var logger: Logger

    private val viewModel: XoViewModel by viewModel { xoViewModelFactory.create() }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        DaggerXoComponent.factory().create(findDependencies()).inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return ComposeView {
            //
        }
    }
}
