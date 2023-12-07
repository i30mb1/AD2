package n7.ad2.camera.internal

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
import n7.ad2.camera.internal.di.DaggerCameraComponent
import n7.ad2.ktx.viewModel
import n7.ad2.ui.ComposeView

internal class CameraFragment(
    override var dependenciesMap: DependenciesMap,
) : Fragment(), HasDependencies {

    @Inject lateinit var cameraViewModelFactory: CameraViewModel.Factory
    @Inject lateinit var logger: Logger

    private val viewModel: CameraViewModel by viewModel { cameraViewModelFactory.create() }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        DaggerCameraComponent.factory().create(findDependencies()).inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return ComposeView {
            //
        }
    }
}
