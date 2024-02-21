package n7.ad2.feature.camera.domain.impl

import androidx.camera.core.Preview
import n7.ad2.feature.camera.domain.Previewer

class PreviewerCameraX(
    private val cameraProvider: CameraProvider,
) : Previewer {

    private val preview: Preview by lazy {
        Preview.Builder().build()
    }

    override fun start(surface: Any) {
        cameraProvider.bind(preview)
        preview.setSurfaceProvider(surface as Preview.SurfaceProvider)
    }
}
