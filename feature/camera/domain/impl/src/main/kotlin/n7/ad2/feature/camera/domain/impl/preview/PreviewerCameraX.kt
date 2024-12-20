package n7.ad2.feature.camera.domain.impl.preview

import android.util.Range
import androidx.camera.core.MirrorMode
import androidx.camera.core.Preview
import androidx.camera.core.UseCase
import n7.ad2.feature.camera.domain.Previewer

class PreviewerCameraX : Previewer {

    private val preview: Preview by lazy {
        Preview.Builder()
            .setTargetFrameRate(Range(60, 60))
            .setMirrorMode(MirrorMode.MIRROR_MODE_OFF)
            .build()
    }

    override fun start(surface: Any): UseCase {
        preview.setSurfaceProvider(surface as Preview.SurfaceProvider)
        return preview
    }
}
