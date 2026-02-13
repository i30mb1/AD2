package n7.ad2.microbenchmark.buffer.buffer

import android.graphics.Bitmap
import n7.ad2.feature.camera.domain.impl.processor.buffer.InputBuffer
import org.tensorflow.lite.support.common.ops.NormalizeOp
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.image.ops.ResizeWithCropOrPadOp
import java.nio.Buffer

// используя классы из TFlite-support мы можем получить Buffer только в виде interleaved (RGBRGBRGB)
public class TFBuffer : InputBuffer {

    override fun get(bitmap: Bitmap): Buffer {
        // сразу получаем тензор из изображения
        val tensorImage: TensorImage = TensorImage.fromBitmap(bitmap)
        // можем переиспользовать обьект загружая каждый раз новое изображение
//        val tensorImage = TensorImage(DataType.FLOAT32)
//        tensorImage.load(bitmap)

        // можно еще напрямую получать ImageProxy, засунуть сюда и пропустить конвертацию в bitmap
        // val mediaImage = MediaMlImageBuilder(imageProxy).build()
        // MlImageAdapter.createTensorImageFrom(mediaImage)

        val imageProcessor = ImageProcessor.Builder()
            .add(NormalizeOp(127.5f, 127.5f))
            .add(ResizeWithCropOrPadOp(128, 128))
            .build()
        return imageProcessor.process(tensorImage).buffer
    }
}
