package n7.ad2.camera.internal.compose

import android.view.ViewGroup
import androidx.camera.core.Preview.SurfaceProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import n7.ad2.camera.internal.model.CameraStateUI
import n7.ad2.ui.compose.AppTheme

@Preview
@Composable
private fun CameraPreview() {
    AppTheme {
        Camera(
            CameraStateUI.init(),
            {},
        )
    }
}

@Composable
internal fun Camera(
    cameraStateUI: CameraStateUI,
    event: (CameraEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box {
        val context = LocalContext.current
        val lifecycle = LocalLifecycleOwner.current
        val previewScaleType = PreviewView.ScaleType.FILL_CENTER
        AndroidView(factory = {
            PreviewView(context).apply {
                scaleType = previewScaleType
                layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
                event(CameraEvent.PreviewReady(surfaceProvider))
            }
        })
        Box(
            modifier = Modifier
                .padding(20.dp)
                .size(128.dp)
                .background(AppTheme.color.primary)
                .align(Alignment.BottomCenter)
                .clickable { event(CameraEvent.Click) }
        ) {
            if (cameraStateUI.image != null) {
                Image(
                    bitmap = cameraStateUI.image.asImageBitmap(),
                    contentDescription = null
                )
            }
        }
    }

}

sealed interface CameraEvent {
    class PreviewReady(
        val surfaceProvider: SurfaceProvider,
    ) : CameraEvent
    data object Click : CameraEvent
}
