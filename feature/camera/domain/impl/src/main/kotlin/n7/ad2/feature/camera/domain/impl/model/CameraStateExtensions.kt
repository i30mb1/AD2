package n7.ad2.feature.camera.domain.impl.model

import androidx.camera.view.PreviewView
import kotlin.math.max
import kotlin.math.min
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import n7.ad2.feature.camera.domain.model.CameraState
import n7.ad2.feature.camera.domain.model.DetectedFace
import n7.ad2.feature.camera.domain.model.Image
import n7.ad2.feature.camera.domain.model.raw

fun MutableStateFlow<CameraState>.setDetectedObject(
    detectedFace: DetectedFace?,
    scaleType: PreviewView.ScaleType,
    viewWidth: Int,
    viewHeight: Int,
    sourceImageWidth: Int,
    sourceImageHeight: Int,
    imageFlipped: Boolean,
) = update {
    if (detectedFace == null || viewWidth == 0 || sourceImageWidth == 0) return@update it.copy(null)
    val scale = if (scaleType == PreviewView.ScaleType.FILL_START ||
        scaleType == PreviewView.ScaleType.FILL_END ||
        scaleType == PreviewView.ScaleType.FILL_CENTER
    ) {
        max(viewWidth.toFloat() / sourceImageWidth, viewHeight.toFloat() / sourceImageHeight)
    } else {
        min(viewWidth.toFloat() / sourceImageWidth, viewHeight.toFloat() / sourceImageHeight)
    }
    val previewImageWidth = sourceImageWidth * scale
    val previewImageHeight = sourceImageHeight * scale
    val (x, y) = when (scaleType) {
        PreviewView.ScaleType.FILL_START, PreviewView.ScaleType.FIT_START -> Pair(0f, 0f)

        PreviewView.ScaleType.FILL_END, PreviewView.ScaleType.FIT_END -> Pair(
            viewWidth - previewImageWidth,
            viewHeight - previewImageHeight
        )

        else -> Pair(
            viewWidth / 2 - previewImageWidth / 2,
            viewHeight / 2 - previewImageHeight / 2,
        )
    }

    it.copy(
        detectedFace = DetectedFace(
            detectedFace.xMin * previewImageWidth + x,
            detectedFace.xMax * previewImageWidth + x,
            detectedFace.yMin * previewImageHeight + y,
            detectedFace.yMax * previewImageHeight + y,
            0f,
        )
    )
}

fun MutableStateFlow<CameraState>.setImage(image: Image) = update {
    it.copy(
        raw = raw(
            image,
            null,
        )
    )
}