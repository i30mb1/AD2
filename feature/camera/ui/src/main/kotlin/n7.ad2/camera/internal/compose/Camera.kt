package n7.ad2.camera.internal.compose

import android.view.ViewGroup
import androidx.camera.core.Preview.SurfaceProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import kotlin.time.DurationUnit
import n7.ad2.camera.internal.model.CameraStateUI
import n7.ad2.feature.camera.ui.R
import n7.ad2.ui.compose.AppTheme

@Preview
@Composable
private fun CameraPreview() {
    AppTheme {
        Camera(
            CameraStateUI(),
            {},
        )
    }
}

@Composable
internal fun Camera(
    state: CameraStateUI,
    event: (CameraEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box {
        val context = LocalContext.current
        val lifecycle = LocalLifecycleOwner.current
        val previewScaleType: PreviewView.ScaleType = PreviewView.ScaleType.FILL_CENTER
        AndroidView(factory = {
            PreviewView(context).apply {
                scaleType = previewScaleType
//                implementationMode = PreviewView.ImplementationMode.COMPATIBLE

                layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
                event(CameraEvent.PreviewReady(surfaceProvider, previewScaleType))
            }
        })
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .onGloballyPositioned { layoutCoordinates ->
                    event(
                        CameraEvent.GloballyPosition(
                            layoutCoordinates.size.width,
                            layoutCoordinates.size.height,
                        )
                    )
                },
            onDraw = {
//                if (state.detectedRect is DetectedRect.Face) {
//                    drawRect(
//                        color = Color.Blue,
//                        topLeft = Offset(state.detectedRect.xMin, state.detectedRect.yMin),
//                        size = Size(
//                            state.detectedRect.xMax - state.detectedRect.xMin,
//                            state.detectedRect.yMax - state.detectedRect.yMin,
//                        ),
//                    )
//                }
            },
        )
        Column(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 50.dp),
        ) {
            Text(
                text = state.timeoutForRecording.toString(DurationUnit.MILLISECONDS),
                style = AppTheme.style.H3,
                color = AppTheme.color.textSecondaryColor,
            )
            Icon(
                painter = painterResource(
                    id = if (state.isRecording) R.drawable.pause else R.drawable.play
                ),
                contentDescription = null,
            )
        }

        Box(
            modifier = Modifier
                .padding(20.dp)
                .size(128.dp)
                .background(AppTheme.color.primary)
                .align(Alignment.BottomCenter)
                .clickable { event(CameraEvent.Click) }
        ) {
            if (state.image != null) {
                Image(
                    bitmap = state.image.asImageBitmap(),
                    contentDescription = null
                )
            }
        }
    }
}

sealed interface CameraEvent {
    class PreviewReady(
        val surfaceProvider: SurfaceProvider,
        val scaleType: PreviewView.ScaleType,
    ) : CameraEvent

    class GloballyPosition(
        val viewWidth: Int,
        val viewHeight: Int,
    ) : CameraEvent

    data object Click : CameraEvent
}
