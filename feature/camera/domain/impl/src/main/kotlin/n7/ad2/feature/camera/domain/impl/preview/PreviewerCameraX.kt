package n7.ad2.feature.camera.domain.impl.preview

import android.util.Range
import androidx.camera.core.Preview
import androidx.camera.core.UseCase
import n7.ad2.feature.camera.domain.Previewer

class PreviewerCameraX : Previewer {

    private val preview: Preview by lazy {
        Preview.Builder()
            .setTargetFrameRate(Range(60, 60))
//            .setPreviewStabilizationEnabled(true)
            .build()
    }

    override fun start(surface: Any): UseCase {
        preview.setSurfaceProvider(surface as Preview.SurfaceProvider)
        return preview
    }
}
