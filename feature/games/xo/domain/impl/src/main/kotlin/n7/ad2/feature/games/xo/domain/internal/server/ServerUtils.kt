package n7.ad2.feature.games.xo.domain.internal.server

import java.io.EOFException
import java.io.InputStream

/**
 * Читает ровно [size] байт: по TCP данные приходят частями, один read() может вернуть меньше
 */
internal fun InputStream.readFully(size: Int): ByteArray {
    val bytes = ByteArray(size)
    var offset = 0
    while (offset < size) {
        val read = read(bytes, offset, size - offset)
        if (read == -1) throw EOFException("Expected $size bytes, got $offset")
        offset += read
    }
    return bytes
}

internal fun InputStream.getBytes(size: Int): ByteArray = readFully(size)

internal fun InputStream.getBytes(size: Long): ByteArray = readFully(size.toInt())
