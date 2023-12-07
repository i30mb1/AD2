package n7.ad2.camera.internal

import android.app.Application
import android.content.Context
import androidx.camera.core.Preview
import javax.inject.Inject

class Previewer @Inject constructor(
    private val application: Application,
    private val lifecycle: CameraLifecycle,
) {

    private val preview: Preview by lazy {
        Preview.Builder().build()
    }

    suspend fun startPreview(surface: Preview.SurfaceProvider) {
        application.getCamera().bindToLifecycle(lifecycle, settings.cameraSelector(), preview)
        preview.setSurfaceProvider(surface)
    }
}
