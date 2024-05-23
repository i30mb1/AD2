package n7.ad2.feature.camera.domain.impl.preview

import androidx.camera.core.Preview
import n7.ad2.feature.camera.domain.Previewer
import n7.ad2.feature.camera.domain.impl.CameraProvider

class PreviewerCameraX(
    private val cameraProvider: CameraProvider,
) : Previewer {

    private val preview: Preview by lazy {
        Preview.Builder()
//            .setPreviewStabilizationEnabled(true)
            .build()
    }

    override fun start(surface: Any) {
        cameraProvider.bind(preview)
        preview.setSurfaceProvider(surface as Preview.SurfaceProvider)
    }
}
