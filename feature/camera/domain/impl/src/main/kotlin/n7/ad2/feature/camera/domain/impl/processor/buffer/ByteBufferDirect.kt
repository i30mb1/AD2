package n7.ad2.feature.camera.domain.impl.processor.buffer

import android.graphics.Bitmap
import android.graphics.Color
import java.nio.Buffer
import java.nio.ByteBuffer
import java.nio.ByteOrder

public class ByteBufferDirect : InputBuffer {

    override fun get(bitmap: Bitmap): Buffer {
        val colorChannelsCount = 3

        /**
         * Выделяем нужный по размеру структуру данных
         */
        val byteBuffer = ByteBuffer
            .allocateDirect(bitmap.width * bitmap.height * colorChannelsCount * Float.SIZE_BYTES)
            .order(ByteOrder.nativeOrder())

        /**
         *  Считываем пиксели, для картинки
         *                           RRR
         *                           GGG
         *                           BBB
         *  получится вот такой массив со значениями в 10-ричном формате
         *          +-----------------------------------+
         *          |           |           |           |
         *     RED  |  -65536   |  -65536   |  -65536   |
         *  пиксели |           |           |           |
         *          |-----------|-----------|-----------|
         *          |           |           |           |
         *   GREEN  | -16711936 | -16711936 | -16711936 |
         *  пиксели |           |           |           |
         *          |-----------|-----------|-----------|
         *          |           |           |           |
         *    BLUE  | -16776961 | -16776961 | -16776961 |
         *  пиксели |           |           |           |
         *          +-----------------------------------+
         *  в двоичном формате первый пиксель выглядел бы 11111111 11111111 00000000 00000000
         *  в 16-ричном 0xFFFF0000
         */
        val pixels = IntArray(bitmap.width * bitmap.height)
        bitmap.getPixels(pixels, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height)

        fillByteBuffer(pixels, byteBuffer)
        byteBuffer.rewind()
        return byteBuffer
    }

    private fun fillByteBuffer(pixels: IntArray, byteBuffer: ByteBuffer) {
        val valuesPerChannel = pixels.size * Float.SIZE_BYTES
        for ((pixelIndex, pixelValue) in pixels.withIndex()) {
            val r = Color.red(pixelValue)
            val g = Color.green(pixelValue)
            val b = Color.blue(pixelValue)

            val redIndex = pixelIndex * Float.SIZE_BYTES
            val greenIndex = redIndex + valuesPerChannel
            val blueIndex = greenIndex + valuesPerChannel
            val normalizedRed = normalizeValue(r)
            val normalizedGreen = normalizeValue(g)
            val normalizedBlue = normalizeValue(b)

            byteBuffer.putFloat(redIndex, normalizedRed)
            byteBuffer.putFloat(greenIndex, normalizedGreen)
            byteBuffer.putFloat(blueIndex, normalizedBlue)
        }
    }

    private fun normalizeValue(value: Int): Float {
        val mean = 127.5f
        val std = 127.5f
        return (value - mean) / std
    }
}
