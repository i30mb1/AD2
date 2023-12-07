package n7.ad2.camera.internal

import androidx.camera.core.Preview
import javax.inject.Inject

class Controller @Inject constructor(
    private val previewer: Previewer,
) {

    suspend fun onUIBind(surfaceProvider: Preview.SurfaceProvider) {
        previewer.startPreview(surfaceProvider)
    }
}
