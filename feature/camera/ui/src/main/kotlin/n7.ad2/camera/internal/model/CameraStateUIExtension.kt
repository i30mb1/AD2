package n7.ad2.camera.internal.model

import androidx.camera.view.PreviewView
import kotlin.math.max
import kotlin.math.min
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import n7.ad2.feature.camera.domain.model.DetectedFaceNormalized
import n7.ad2.feature.camera.domain.model.Image

internal fun MutableStateFlow<CameraStateUI>.setFace(
    detectedFaceNormalized: DetectedFaceNormalized?,
    image: Image?,
) {
    if (detectedFaceNormalized == null || image == null) return
    update { state ->

        val scale = when (state.scaleType) {
            PreviewView.ScaleType.FILL_START, PreviewView.ScaleType.FILL_END, PreviewView.ScaleType.FILL_CENTER -> {
                max(state.viewWidth.toFloat() / image.metadata.width, state.viewHeight.toFloat() / image.metadata.height)
            }

            else -> {
                min(state.viewWidth.toFloat() / image.metadata.width, state.viewHeight.toFloat() / image.metadata.height)
            }
        }
        val previewImageWidth = image.metadata.width * scale
        val previewImageHeight = image.metadata.height * scale
        val (x, y) = when (state.scaleType) {
            PreviewView.ScaleType.FILL_START, PreviewView.ScaleType.FIT_START -> Pair(0f, 0f)

            PreviewView.ScaleType.FILL_END, PreviewView.ScaleType.FIT_END -> Pair(
                state.viewWidth - previewImageWidth, state.viewHeight - previewImageHeight
            )

            else -> Pair(
                state.viewWidth / 2 - previewImageWidth / 2,
                state.viewHeight / 2 - previewImageHeight / 2,
            )
        }

        state.copy(
            detectedRect = DetectedRect.Face(
                detectedFaceNormalized.xMin * previewImageWidth + x,
                detectedFaceNormalized.xMax * previewImageWidth + x,
                detectedFaceNormalized.yMin * previewImageHeight + y,
                detectedFaceNormalized.yMax * previewImageHeight + y,
            )
        )
    }
}

internal fun MutableStateFlow<CameraStateUI>.setScaleType(scaleType: PreviewView.ScaleType) = update { state ->
    state.copy(scaleType = scaleType)
}

internal fun MutableStateFlow<CameraStateUI>.setPreviewSizes(viewHeight: Int, viewWidth: Int) = update { state ->
    state.copy(viewHeight = viewHeight, viewWidth = viewWidth)
}
