package n7.ad2.feature.camera.domain.impl.processor

import android.app.Application
import android.graphics.Bitmap
import n7.ad2.feature.camera.domain.FaceDetect
import n7.ad2.feature.camera.domain.impl.processor.buffer.ByteBufferDirect
import n7.ad2.feature.camera.domain.impl.processor.buffer.InputBuffer
import n7.ad2.feature.camera.domain.model.DetectedFaceNormalized
import n7.ad2.feature.camera.domain.model.Image
import kotlin.math.abs
import kotlin.math.exp
import kotlin.math.max
import kotlin.math.min

@Suppress("MagicNumber")
class ProcessorTF(application: Application, private val getInput: InputBuffer = ByteBufferDirect()) : FaceDetect {

    private val interpreter = GetMLModelChannel(application, MODEL_ASSET).get()
    private val priors: List<Prior> = generatePriors()
    private val priorCount = priors.size
    private val scoresIndex: Int
    private val boxesIndex: Int

    init {
        interpreter.allocateTensors()
        val firstIsScores = interpreter.getOutputTensor(0).shape().last() == CLASSES_COUNT
        scoresIndex = if (firstIsScores) 0 else 1
        boxesIndex = if (firstIsScores) 1 else 0
    }

    override fun detect(image: Image): List<DetectedFaceNormalized> {
        val bitmap = image.source as Bitmap
        val resized = Bitmap.createScaledBitmap(bitmap, INPUT_WIDTH, INPUT_HEIGHT, true)
        val inputBuffer = getInput.get(resized)

        val scores = Array(1) { Array(priorCount) { FloatArray(CLASSES_COUNT) } }
        val boxes = Array(1) { Array(priorCount) { FloatArray(4) } }
        val inputs: Array<Any> = arrayOf(inputBuffer)
        val outputs: Map<Int, Any> = hashMapOf(scoresIndex to scores, boxesIndex to boxes)
        interpreter.runForMultipleInputsOutputs(inputs, outputs)

        return nms(decode(scores[0], boxes[0])).map { face ->
            DetectedFaceNormalized(face.xMin, face.xMax, face.yMin, face.yMax, face.score)
        }
    }

    private fun decode(scores: Array<FloatArray>, boxes: Array<FloatArray>): List<Face> = buildList {
        for (i in 0 until priorCount) {
            val probability = probability(scores[i])
            if (probability <= PROBABILITY_THRESHOLD) continue
            val prior = priors[i]
            val location = boxes[i]
            val centerX = location[0] * CENTER_VARIANCE * prior.width + prior.centerX
            val centerY = location[1] * CENTER_VARIANCE * prior.height + prior.centerY
            val width = exp(location[2] * SIZE_VARIANCE) * prior.width
            val height = exp(location[3] * SIZE_VARIANCE) * prior.height
            add(
                Face(
                    clamp(centerX - width / 2),
                    clamp(centerX + width / 2),
                    clamp(centerY - height / 2),
                    clamp(centerY + height / 2),
                    probability,
                ),
            )
        }
    }

    private fun probability(scores: FloatArray): Float {
        val background = scores[0]
        val face = scores[1]
        if (abs(background + face - 1f) < 0.01f) return face
        val maxLogit = max(background, face)
        val expBackground = exp(background - maxLogit)
        val expFace = exp(face - maxLogit)
        return expFace / (expBackground + expFace)
    }

    /**
     * Алгоритм Non-maximum Suppression (NMS) помогает уменьшить количество перекрывающихся bounding boxes,
     * выбирая наиболее вероятный или "лучший" для каждого обнаруженного объекта
     * Этот алгоритм удаляет меньшие bounding boxes, которые слишком сильно перекрываются с большей
     */
    private fun nms(faces: List<Face>): List<Face> {
        if (faces.isEmpty()) return emptyList()

        val sortedBoxes = faces.toMutableList()
        sortedBoxes.sortBy { it.score }

        val result = mutableListOf<Face>()
        while (sortedBoxes.isNotEmpty()) {
            val box = sortedBoxes.removeAt(sortedBoxes.lastIndex)
            result.add(box)
            sortedBoxes.removeAll { iou(box, it) >= IOU_THRESHOLD }
        }
        result.sortByDescending { it.area }

        return result
    }

    private fun iou(box1: Face, box2: Face): Float {
        val xMin = max(box1.xMin, box2.xMin)
        val yMin = max(box1.yMin, box2.yMin)
        val xMax = min(box1.xMax, box2.xMax)
        val yMax = min(box1.yMax, box2.yMax)
        val overlap = max(0f, xMax - xMin) * max(0f, yMax - yMin)
        return overlap / (box1.area + box2.area - overlap + Float.MIN_VALUE)
    }

    private fun generatePriors(): List<Prior> = buildList {
        for (index in STRIDES.indices) {
            val stride = STRIDES[index]
            val scaleW = INPUT_WIDTH.toFloat() / stride
            val scaleH = INPUT_HEIGHT.toFloat() / stride
            val featureMapW = ceilDiv(INPUT_WIDTH, stride)
            val featureMapH = ceilDiv(INPUT_HEIGHT, stride)
            for (y in 0 until featureMapH) {
                for (x in 0 until featureMapW) {
                    val centerX = (x + 0.5f) / scaleW
                    val centerY = (y + 0.5f) / scaleH
                    for (boxSize in MIN_BOXES[index]) {
                        add(
                            Prior(
                                centerX,
                                centerY,
                                clamp(boxSize / INPUT_WIDTH.toFloat()),
                                clamp(boxSize / INPUT_HEIGHT.toFloat()),
                            ),
                        )
                    }
                }
            }
        }
    }

    private fun ceilDiv(a: Int, b: Int): Int = (a + b - 1) / b

    private fun clamp(value: Float): Float = min(1f, max(0f, value))

    private class Prior(val centerX: Float, val centerY: Float, val width: Float, val height: Float)

    private class Face(val xMin: Float, val xMax: Float, val yMin: Float, val yMax: Float, val score: Float) {
        val area = (xMax - xMin) * (yMax - yMin)
    }

    companion object {
        private const val MODEL_ASSET = "ultraface_rfb_320.tflite"
        const val INPUT_WIDTH = 320
        const val INPUT_HEIGHT = 240
        private const val CLASSES_COUNT = 2
        private const val PROBABILITY_THRESHOLD = 0.7f
        private const val CENTER_VARIANCE = 0.1f
        private const val SIZE_VARIANCE = 0.2f
        private const val IOU_THRESHOLD = 0.3f
        private val STRIDES = intArrayOf(8, 16, 32, 64)
        private val MIN_BOXES = arrayOf(
            floatArrayOf(10f, 16f, 24f),
            floatArrayOf(32f, 48f),
            floatArrayOf(64f, 96f),
            floatArrayOf(128f, 192f, 256f),
        )
    }
}
