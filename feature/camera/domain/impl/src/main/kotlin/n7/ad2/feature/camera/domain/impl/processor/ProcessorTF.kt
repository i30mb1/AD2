package n7.ad2.feature.camera.domain.impl.processor

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import androidx.annotation.FloatRange
import n7.ad2.feature.camera.domain.impl.processor.buffer.InputBuffer
import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import kotlin.math.max
import kotlin.math.min

/**
 * @author e.shuvagin
 */
public class MyTF(private val getMLModel: GetMLModel, private val getInput: InputBuffer) {

    public fun analyze(originalBitmap: Bitmap): DetectedRect {
        // загружаем модель
        val interpreter = getMLModel.get()
        // подготавливаем массивы где будут хранится результаты
//        val outputBoxes = Array(1) { Array(4420) { FloatArray(4) } }
        val outputBoxes2 = TensorBuffer.createFixedSize(intArrayOf(1, 4420, 4), DataType.FLOAT32)
        val outputScores = Array(1) { Array(4420) { FloatArray(2) } }
        val outputs: Map<Int, Any> = hashMapOf(0 to outputBoxes2.buffer, 1 to outputScores)

        // подготавливаем массив с входными данными
        val bitmap = Bitmap.createScaledBitmap(originalBitmap, 320, 240, true)
        val inputs = arrayOf(getInput.get(bitmap))
        interpreter.runForMultipleInputsOutputs(inputs, outputs)

        val detectedFaces: List<DetectedFace> = buildList {
            val boxes = outputBoxes2.floatArray
            val scores = outputScores[0]
            for (i in scores.indices) {
                val probability = scores[i][1]
                if (probability > 0.7f) {
                    val left = boxes[i * 4]
                    val top = boxes[i * 4 + 1]
                    val right = boxes[i * 4 + 2]
                    val bottom = boxes[i * 4 + 3]
                    add(DetectedFace(DetectedRectF(left, right, top, bottom), probability))
                }
            }
        }

        /** Пропускаем результаты через NMS */
        val nmsFaceDetectOutputs: List<DetectedFace> = nms(detectedFaces)
        val normalizedFaces: List<DetectedRectF> = nmsFaceDetectOutputs.map(DetectedFace::rect)
        val absoluteFaces: List<DetectedRect> = nmsFaceDetectOutputs.map { face ->
            face.rect.toDetectedRect(320, 240, bitmap.width, bitmap.height, face.score)
        }
        val face = absoluteFaces.first()

        // обводим лицо по имеющимся координатам
        val canvas = Canvas(bitmap)
        canvas.drawRect(face.xMin.toFloat(), face.yMin.toFloat(), face.xMax.toFloat(), face.yMax.toFloat(), Paint())
        return face
    }

    /** Нормализованные (0.0 - 1.0) координаты лица переводим в абсолютные (0 - Int.MAX_VALUE) */
    private fun DetectedRectF.toDetectedRect(detectedRectWidth: Int, detectedRectHeight: Int, toWidth: Int, toHeight: Int, score: Float): DetectedRect {
        /** Рассчитываем коэффициенты масштабирования для ширины и высоты */
        val scaleX = toWidth.toFloat() / detectedRectWidth.toFloat()
        val scaleY = toHeight.toFloat() / detectedRectHeight.toFloat()

        /** Преобразуем координаты к разрешению изображения */
        return DetectedRect(
            (xMin * detectedRectWidth * scaleX).toInt(),
            (xMax * detectedRectWidth * scaleX).toInt(),
            (yMin * detectedRectHeight * scaleY).toInt(),
            (yMax * detectedRectHeight * scaleY).toInt(),
            score,
        )
    }

    /**
     * Алгоритм Non-maximum Suppression (NMS) помогает уменьшить количество перекрывающихся bounding boxes,
     * выбирая наиболее вероятный или "лучший" для каждого обнаруженного объекта
     * Этот алгоритм удаляет меньшие bounding boxes, которые слишком сильно перекрываются с большей
     */
    private fun nms(faces: List<DetectedFace>): List<DetectedFace> {
        if (faces.isEmpty()) return emptyList()

        val sortedBoxes = faces.toMutableList()
        sortedBoxes.sortBy { it.score }

        val result = mutableListOf<DetectedFace>()
        while (sortedBoxes.isNotEmpty()) {
            val box = sortedBoxes.removeAt(sortedBoxes.lastIndex)
            result.add(box)

            val iouThreshold = 0.3f
            sortedBoxes.removeAll { detectedObject ->
                iou(box.rect, detectedObject.rect) >= iouThreshold
            }
        }
        result.sortByDescending { it.rect.area }

        return result
    }

    private fun iou(box1: DetectedRectF, box2: DetectedRectF): Float {
        val xMin = max(box1.xMin, box2.xMin)
        val yMin = max(box1.yMin, box2.yMin)
        val xMax = min(box1.xMax, box2.xMax)
        val yMax = min(box1.yMax, box2.yMax)
        val overlap = (xMax - xMin) * (yMax - yMin)
        val eps = Float.MIN_VALUE
        return overlap / (box1.area + box2.area - overlap + eps)
    }

    private class DetectedFace(val rect: DetectedRectF, val score: Float)

    /** Лицо в нормированных координатах */
    private data class DetectedRectF(@FloatRange(from = 0.0, to = 1.0) val xMin: Float, @FloatRange(from = 0.0, to = 1.0) val xMax: Float, @FloatRange(from = 0.0, to = 1.0) val yMin: Float, @FloatRange(from = 0.0, to = 1.0) val yMax: Float) {
        val area = (xMax - xMin) * (yMax - yMin)
    }
}

/** Лицо в абсолютных координатах привязанные к определенной bitmap */
public data class DetectedRect(val xMin: Int, val xMax: Int, val yMin: Int, val yMax: Int, val score: Float)

public fun DetectedRect.width(): Int = xMax - xMin

public fun DetectedRect.height(): Int = yMax - yMin
