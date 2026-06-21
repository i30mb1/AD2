package n7.ad2.feature.camera.domain.model

import n7.ad2.feature.camera.domain.Rotation
import java.lang.Float.max
import java.lang.Float.min

class ProcessorState(
    val image: Image,
    val faces: List<DetectedFace> = emptyList(),
    val illumination: Illumination = Illumination(NORMAL_BRIGHTNESS),
    val modelsName: List<String> = emptyList(),
) {
    companion object {
        private const val NORMAL_BRIGHTNESS = 0.5f
    }
}

/**
 * occlusionScore - закрытость лица (0 - открыто / 1 - закрыто(шарфом, очками))
 */
data class DetectedFace(
    val rect: DetectedFaceNormalized,
    val rotation: Rotation = Rotation.ZERO,
    val occlusionScore: Float = 0f,
    val blurriness: Float = 0f,
)

class Illumination(val brightness: Float) {
    init {
        require(brightness in 0.0f..1.0f) { "Яркость должна быть в диапазоне [0.0, 1.0], но было $brightness" }
    }
}

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
