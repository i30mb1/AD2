package n7.ad2.feature.camera.domain.impl.processing

import android.graphics.Bitmap
import kotlin.math.max
import kotlin.math.min

class FaceFromPhotoExtractor(
    private val imageEditor: ImageEditor,
) {

    /**
     * Вырезает лицо с отступами 20% (как expand_bbox в ML-пайплайне: расширение во float, усечение в конце)
     */
    fun extract(bitmap: Bitmap, face: FaceBox): Bitmap? {
        val expandWidth = (face.endX - face.startX) * FACE_PADDING
        val expandHeight = (face.endY - face.startY) * FACE_PADDING
        val xMin = max(0f, face.startX - expandWidth).toInt()
        val yMin = max(0f, face.startY - expandHeight).toInt()
        val xMax = min(bitmap.width.toFloat(), face.endX + expandWidth).toInt()
        val yMax = min(bitmap.height.toFloat(), face.endY + expandHeight).toInt()
        return imageEditor.crop(bitmap, xMin, yMin, xMax - xMin, yMax - yMin)
    }

    private companion object {
        private const val FACE_PADDING = 0.2f
    }
}
