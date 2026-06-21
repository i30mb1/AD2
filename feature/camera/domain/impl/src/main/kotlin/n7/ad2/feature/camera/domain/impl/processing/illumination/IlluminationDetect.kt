package n7.ad2.feature.camera.domain.impl.processing.illumination

import android.graphics.Bitmap
import androidx.core.graphics.blue
import androidx.core.graphics.green
import androidx.core.graphics.red
import n7.ad2.feature.camera.domain.CameraSettings
import n7.ad2.feature.camera.domain.impl.processing.ImageEditor
import n7.ad2.feature.camera.domain.model.Illumination

/**
 * Определяет яркость всего кадра без качества освещения лица.
 */
class IlluminationDetect(
    private val settings: CameraSettings,
    private val imageEditor: ImageEditor,
) {

    private var pixels: IntArray? = null

    fun inference(bitmap: Bitmap): Illumination {
        return if (settings.isIlluminationEnabled) process(bitmap) else Illumination(0.5f)
    }

    @Suppress("MagicNumber", "TooGenericExceptionCaught")
    private fun process(bitmap: Bitmap): Illumination {
        val sample = downsample(bitmap)
        val area = sample.width * sample.height
        val pixels = pixels?.takeIf { it.size == area } ?: IntArray(area).also { this.pixels = it }
        var brightness = 0f
        try {
            sample.getPixels(pixels, 0, sample.width, 0, 0, sample.width, sample.height)
            for (pixel in pixels) {
                // https://gist.github.com/maxjvh/b1d772caf76cdc0c11e2
                brightness += pixel.red + pixel.green + pixel.blue
            }
            val result = (brightness / (area * 3f * 255f)).coerceIn(0.0f, 1.0f)
            return Illumination(result)
        } catch (error: Exception) {
            return Illumination(0f)
        } finally {
            if (sample !== bitmap) sample.recycle()
        }
    }

    private fun downsample(bitmap: Bitmap): Bitmap {
        if (bitmap.width <= SAMPLE_SIDE && bitmap.height <= SAMPLE_SIDE) return bitmap
        return imageEditor.resize(bitmap, SAMPLE_SIDE, SAMPLE_SIDE)
    }

    private companion object {
        private const val SAMPLE_SIDE = 128
    }
}
