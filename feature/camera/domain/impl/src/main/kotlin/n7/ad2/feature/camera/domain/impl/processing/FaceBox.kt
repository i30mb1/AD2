package n7.ad2.feature.camera.domain.impl.processing

import n7.ad2.feature.camera.domain.model.DetectedFaceNormalized

class FaceBox(val startX: Float, val startY: Float, val endX: Float, val endY: Float)

fun DetectedFaceNormalized.toFaceBox(width: Int, height: Int): FaceBox =
    FaceBox(xMin * width, yMin * height, xMax * width, yMax * height)
