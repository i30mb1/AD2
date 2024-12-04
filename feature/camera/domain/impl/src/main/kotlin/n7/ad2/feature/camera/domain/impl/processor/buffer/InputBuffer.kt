package n7.ad2.feature.camera.domain.impl.processor.buffer

import android.graphics.Bitmap
import java.nio.Buffer

// затестить MappedByteBuffer вместо ByteBuffer
public interface InputBuffer {
    /**
     * @return структура данных каналов пикселей в планаре (RRRGGGBBB)
     */
    public fun get(bitmap: Bitmap): Buffer
}
