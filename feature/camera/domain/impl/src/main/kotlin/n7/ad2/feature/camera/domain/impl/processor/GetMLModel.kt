package n7.ad2.feature.camera.domain.impl.processor

import android.content.Context
import java.io.FileInputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.MappedByteBuffer
import java.nio.channels.Channels
import java.nio.channels.FileChannel
import org.tensorflow.lite.Interpreter

public fun interface GetMLModel {
    public fun get(): Interpreter
}

private val options = Interpreter.Options().apply {
//    if (compatList.isDelegateSupportedOnThisDevice) { // 13,609,040   ns
//        // if the device has a supported GPU, add the GPU delegate
//        val delegateOptions = compatList.bestOptionsForThisDevice
//        addDelegate(GpuDelegate(delegateOptions)) // 13,558,973   ns
//    } else {
//        // if the GPU is not supported, run on 4 threads
//        numThreads = 4 // 13,705,186   ns
//    }
}

// быстрее на 10% GetMLModelReadBytes
public class GetMLModelChannel(
    private val context: Context,
) : GetMLModel {
    override fun get(): Interpreter {
        val input = context.assets.open("blaze_face.tflite")
        val buffer = ByteBuffer.allocateDirect(input.available())
        buffer.order(ByteOrder.nativeOrder())
        Channels.newChannel(input).read(buffer)
        val interpreter: Interpreter = Interpreter(buffer, options)
        input.close()
        return interpreter
    }
}

public class GetMLModelReadBytes(
    private val context: Context,
) : GetMLModel {
    override fun get(): Interpreter {
        val input = context.assets.open("blaze_face.tflite")
        val buffer = ByteBuffer.allocateDirect(input.available())
        buffer.order(ByteOrder.nativeOrder())
        buffer.put(input.readBytes())
        val interpreter: Interpreter = Interpreter(buffer)
        return interpreter
    }
}

public class GetMLModelReadBytes2(
    private val context: Context,
) : GetMLModel {
    override fun get(): Interpreter {
        val fileDescriptor = context.assets.openFd("blaze_face.tflite")
        val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
        val fileChannel = inputStream.channel
        val startOffset = fileDescriptor.startOffset
        val declaredLength = fileDescriptor.declaredLength
        val ff: MappedByteBuffer = fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
        val interpreter: Interpreter = Interpreter(ff)
        return interpreter
    }
}

//public class GetMLModelLiteRT(
//    private val context: Context,
//) : GetMLModel {
//    override fun get(): Interpreter {
//        val model: MappedByteBuffer = FileUtil.loadMappedFile(context, "blaze_face.tflite")
//        // еще такое есть
////        val model2: Model = Model.createModel(context, "blaze_face.tflite")
//        val interpreter = Interpreter(model)
//        return interpreter
//    }
//}
