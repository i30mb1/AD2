package n7.ad2.feature.camera.domain.impl.processor.buffer.fill

import java.nio.FloatBuffer

public interface FillBuffer {

    /**
     * Заполняем
     * Пиксель за пикселем разбиваем на RGB значения и кладем сначала все RRRR потом GGGG потом BBBB
     * + нормализуем до значений от -1.0 до 1.0 для нашей ML модели
     *   +-----------------------------------+
     *   |                                   |
     *   | -1.0 до 1.0 (Red)                 |
     *   |                                   |
     *   |-----------------------------------|
     *   |                                   |
     *   | -1.0 до 1.0 (Green)               |
     *   |                                   |
     *   |-----------------------------------|
     *   |                                   |
     *   | -1.0 до 1.0 (Blue)                |
     *   |                                   |
     *   +-----------------------------------+
     *
     */
    public fun fillBuffer(pixels: IntArray, byteBuffer: FloatBuffer)

    public fun normalizeValue(value: Int): Float {
        val mean = 127.5f
        val std = 127.5f
        return (value - mean) / std
    }
}
