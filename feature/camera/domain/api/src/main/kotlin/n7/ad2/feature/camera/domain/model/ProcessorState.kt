package n7.ad2.feature.camera.domain.model

import java.lang.Float.max
import java.lang.Float.min

class ProcessorState(val image: Image, val detectedFaceNormalized: DetectedFaceNormalized?)

/**
 * Нормализованные координаты лица от 0.0 до 1.0
 */
data class DetectedFaceNormalized(val xMin: Float, val xMax: Float, val yMin: Float, val yMax: Float, val probability: Float) {
    fun map(mapping: (Float, Float) -> Pair<Float, Float>): DetectedFaceNormalized {
        val (x1, y1) = mapping(xMin, yMin)
        val (x2, y2) = mapping(xMax, yMax)
        return DetectedFaceNormalized(min(x1, x2), max(x1, x2), min(y1, y2), max(y1, y2), probability)
    }
}
