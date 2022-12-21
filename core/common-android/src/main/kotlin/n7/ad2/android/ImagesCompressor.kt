package n7.ad2.android

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.graphics.Matrix
import android.net.Uri
import androidx.exifinterface.media.ExifInterface
import java.io.ByteArrayOutputStream
import java.io.File
import java.lang.Integer.max

class ImagesCompressor {

    companion object {
        private const val MAX_SIZE = 10_000_000 // 10mb
        private const val MAX_HEIGHT = 1000
        private const val MAX_WIDTH = 1000
    }

    suspend fun compressImage(context: Context, uri: Uri): ByteArray {
        val origin = uri.getBitmap(context, false) ?: error("could not get bitmap")
        val bitmap = uri.getBitmap(context, true) ?: error("could not get bitmap")
        val resultWebpLossy = compress(bitmap, Bitmap.CompressFormat.WEBP)
        val resultJpeg = compress(bitmap, Bitmap.CompressFormat.JPEG)
        val resultOrig = compress(origin, Bitmap.CompressFormat.JPEG)
        val bitmapWebp = BitmapFactory.decodeByteArray(resultWebpLossy, 0, resultWebpLossy.size)
        File(context.getExternalFilesDir(null), "Jpeg.jpeg").writeBytes(resultJpeg)
        File(context.getExternalFilesDir(null), "original.jpeg").writeBytes(resultOrig)
        File(context.getExternalFilesDir(null), "Webp.webp").writeBytes(resultWebpLossy)

        return resultWebpLossy
    }

    private fun getBitmap(context: Context, uri: Uri) {
        val source = ImageDecoder.createSource(context.contentResolver, uri)
        val bitmap: Bitmap = ImageDecoder.decodeBitmap(source)
        // or MediaStore.Images.Media.getBitmap()
    }

    private fun compress(bitmap: Bitmap, format: Bitmap.CompressFormat): ByteArray {
        return ByteArrayOutputStream().use { baos ->
            var quality = 100
            bitmap.compress(format, quality, baos)
            while (baos.size() > MAX_SIZE) {
                baos.reset()
                quality -= 5
                bitmap.compress(format, quality, baos)
            }
            baos.toByteArray()
        }
    }

    private fun Uri.getBitmap(context: Context, withSampleSize: Boolean): Bitmap? {
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        context.contentResolver.openInputStream(this).use { BitmapFactory.decodeStream(it, null, options) }
        val exif = context.contentResolver.openInputStream(this)?.use { ExifInterface(it) }
        options.inSampleSize = if (withSampleSize) calculateInSampleSize(options, MAX_WIDTH, MAX_HEIGHT) else 1
        options.inJustDecodeBounds = false
        val bitmap = context.contentResolver.openInputStream(this)?.use {
            BitmapFactory.decodeStream(it, null, options)
        } ?: return null
        val exifOrientation = exif?.getAttributeInt(ExifInterface.TAG_ORIENTATION, 0)
        val matrix = Matrix()
        when (exifOrientation) {
            6 -> matrix.postRotate(90F)
            3 -> matrix.postRotate(180F)
            8 -> matrix.postRotate(270F)
        }
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }

    private fun calculateInSampleSize(options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int {
        val (height: Int, width: Int) = options.run { outHeight to outWidth }
        if (height > reqHeight || width > reqWidth) {
            val heightRatio = height / reqHeight
            val widthRatio = width / reqWidth
            return max(heightRatio, widthRatio) + 1
        }
        return 1
    }

}