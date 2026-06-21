package n7.ad2.feature.camera.domain.impl.processing

abstract class TfLiteModel {

    abstract val interpreter: MLInterpreter
    abstract val modelName: String
    abstract val modelVersion: String

    fun runInference(inputs: Array<Any>, outputs: Map<Int, Any>) {
        interpreter.runForMultipleInputsOutputs(inputs, outputs)
    }

    fun close() {
        interpreter.close()
    }
}
