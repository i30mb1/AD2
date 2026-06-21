package n7.ad2.feature.camera.domain.impl.processing

import android.content.Context
import java.io.InputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder

interface ModelManager {
    fun getMLModel(model: MLModelName): MLModel
    fun thresholds(): ModelThresholds
}

class ModelManagerAndroid(private val context: Context) : ModelManager {

    private val cache = mutableMapOf<MLModelName, MLModel>()

    override fun getMLModel(model: MLModelName): MLModel = cache.getOrPut(model) { load(model) }

    override fun thresholds(): ModelThresholds = ModelThresholds()

    private fun load(modelName: MLModelName): MLModel = with(context.assets) {
        val fileName = list(ROOT_ASSET_FOLDER).orEmpty().filterNotNull().first { params(it).name == modelName }
        create(params(fileName), open("$ROOT_ASSET_FOLDER/$fileName").use { it.toByteBuffer() })
    }

    private fun params(fileName: String): MLModel.Params {
        val (name, version) = fileName.substringBefore(DOTTED_MODEL_EXTENSION).split(DELIMITER)
        return ParamsImpl(MLModelName from name, version)
    }

    private fun create(params: MLModel.Params, bytes: ByteBuffer): MLModel {
        val interpreter = MLInterpreterAndroid(bytes)
        return when (params.name) {
            MLModelName.HEADPOSE_OCCLUSION -> MLModel.HeadPose(interpreter, params)
            MLModelName.TFACE_LITE -> MLModel.TFaceLite(interpreter, params)
            MLModelName.BLAZE_FACE -> MLModel.BlazeFace(interpreter, params)
        }
    }

    companion object {
        private const val ROOT_ASSET_FOLDER = "faceshot"
        private const val DELIMITER = '-'
        private const val DOTTED_MODEL_EXTENSION = ".tflite"
    }
}

fun InputStream.toByteBuffer(): ByteBuffer {
    val bytes = readBytes()
    val buffer = ByteBuffer.allocateDirect(bytes.size)
    buffer.order(ByteOrder.nativeOrder())
    buffer.put(bytes)
    buffer.rewind()
    return buffer
}
