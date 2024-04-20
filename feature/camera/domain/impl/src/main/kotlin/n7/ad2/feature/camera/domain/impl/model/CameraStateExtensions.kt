package n7.ad2.feature.camera.domain.impl.model

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import n7.ad2.feature.camera.domain.model.CameraState
import n7.ad2.feature.camera.domain.model.DetectedFaceNormalized
import n7.ad2.feature.camera.domain.model.Image

internal fun MutableStateFlow<CameraState>.setFace(face: DetectedFaceNormalized?) = update { state ->
    state.copy(detectedFaceNormalized = face)
}

internal fun MutableStateFlow<CameraState>.setImage(image: Image) = update { state ->
    state.copy(image = image)
}
