package n7.ad2.camera.internal.compose

import android.view.ViewGroup
import androidx.camera.core.Preview.*
import androidx.camera.view.PreviewView
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView
import n7.ad2.ui.compose.AppTheme

@Preview
@Composable
internal fun CameraPreview() {
    AppTheme {
        Camera({})
    }
}

@Composable
fun Camera(
    event: (CameraEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val lifecycle = LocalLifecycleOwner.current
    AndroidView(factory = {
        PreviewView(context).apply {
            scaleType = PreviewView.ScaleType.FILL_CENTER
            layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            event(CameraEvent.PreviewReady(surfaceProvider))
        }
    })
}

sealed interface CameraEvent {
    class PreviewReady(val surfaceProvider: SurfaceProvider): CameraEvent
}
