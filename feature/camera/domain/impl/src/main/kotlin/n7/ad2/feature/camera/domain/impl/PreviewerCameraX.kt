package n7.ad2.feature.camera.domain.impl

import android.app.Application
import androidx.camera.core.Preview
import androidx.lifecycle.LifecycleOwner
import n7.ad2.feature.camera.domain.CameraSettings
import n7.ad2.feature.camera.domain.Previewer

class PreviewerCameraX(
    private val application: Application,
    private val lifecycle: LifecycleOwner,
    private val settings: CameraSettings,
) : Previewer {

    private val preview: Preview by lazy {
        Preview.Builder().build()
    }

    override suspend fun start(surface: Any) {
        application.getCamera().bindToLifecycle(lifecycle, settings.cameraSelector(), preview)
        preview.setSurfaceProvider(surface as Preview.SurfaceProvider)
    }
}
