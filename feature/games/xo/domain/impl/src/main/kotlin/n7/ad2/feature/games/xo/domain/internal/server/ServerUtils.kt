package n7.ad2.feature.games.xo.domain.internal.server

import java.io.InputStream

internal fun InputStream.getBytes(size: Int): ByteArray {
    return ByteArray(size).apply { read(this) }
}

internal fun InputStream.getBytes(size: Long): ByteArray {
    return getBytes(size.toInt())
}
