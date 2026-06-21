package n7.ad2.feature.camera.domain.impl.processing

import org.tensorflow.lite.Interpreter
import java.nio.ByteBuffer

interface MLInterpreter {
    fun runForMultipleInputsOutputs(inputs: Array<Any>, outputs: Map<Int, Any>)
    fun close()
}

class MLInterpreterAndroid(bytes: ByteBuffer) : MLInterpreter {

    private val interpreter by lazy { Interpreter(bytes) }

    override fun runForMultipleInputsOutputs(inputs: Array<Any>, outputs: Map<Int, Any>) {
        interpreter.runForMultipleInputsOutputs(inputs, outputs)
    }

    override fun close() {
        interpreter.close()
    }
}
