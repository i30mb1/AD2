package n7.ad2.parser

import java.io.File
import java.net.HttpURLConnection
import java.net.URL

internal fun saveImage(url: String, path: String, name: String) {
    try {
        val pathFormatted = path.lowercase().replace(" ", "_")
        saveImageInternal(url, pathFormatted, name)
    } catch (e: Exception) {
        println("Error for $url, name $name")
        throw e
    }
}

private fun saveImageInternal(url: String, path: String, name: String) {
    val directory = File(path)
    directory.mkdirs()

    // Skip if any known format already exists and is non-empty
    if (listOf("png", "webp", "jpg", "jpeg").any { ext ->
            val f = File(path, "$name.$ext")
            f.exists() && f.length() > 0
        }) return

    val (bytes, contentType) = downloadImageBytes(url)
    if (bytes.isEmpty()) error("empty response for $name")

    val ext = when {
        contentType.contains("webp") -> "webp"
        contentType.contains("jpeg") || contentType.contains("jpg") -> "jpg"
        else -> "png"
    }
    val file = File(path, "$name.$ext")
    file.writeBytes(bytes)
    println("image $name downloaded (${bytes.size} bytes, $ext)")
}

private fun downloadImageBytes(url: String): Pair<ByteArray, String> {
    var connection = openConnection(url)

    // Follow cross-domain redirects (instanceFollowRedirects only works within same domain)
    var redirectCount = 0
    while (connection.responseCode in 300..399 && redirectCount < 5) {
        val newUrl = connection.getHeaderField("Location") ?: break
        connection.disconnect()
        connection = openConnection(newUrl)
        redirectCount++
    }

    val code = connection.responseCode
    if (code !in 200..299) error("HTTP $code for $url")

    val contentType = connection.contentType ?: "image/png"
    val bytes = connection.inputStream.use { it.readBytes() }
    connection.disconnect()
    return bytes to contentType
}

private fun openConnection(url: String): HttpURLConnection {
    val conn = URL(url).openConnection() as HttpURLConnection
    conn.setRequestProperty(
        "User-Agent",
        "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/124.0.0.0 Safari/537.36",
    )
    conn.setRequestProperty("Referer", "https://dota2.fandom.com/")
    conn.setRequestProperty("Accept", "image/avif,image/webp,image/apng,image/svg+xml,image/*,*/*;q=0.8")
    conn.instanceFollowRedirects = true
    conn.connectTimeout = 15000
    conn.readTimeout = 15000
    conn.connect()
    return conn
}
