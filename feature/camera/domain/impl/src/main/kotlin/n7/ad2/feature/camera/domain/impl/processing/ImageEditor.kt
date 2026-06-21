package n7.ad2.feature.camera.domain.impl.processing

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Paint
import androidx.core.graphics.createBitmap
import androidx.core.graphics.scale
import kotlin.math.roundToInt

class ImageEditor {

    private val filterPaint = Paint(Paint.FILTER_BITMAP_FLAG)

    fun resize(bitmap: Bitmap, width: Int, height: Int): Bitmap {
        if (width == bitmap.width && height == bitmap.height) return bitmap
        return bitmap.scale(width, height, true)
    }

    fun crop(bitmap: Bitmap, x: Int, y: Int, width: Int, height: Int): Bitmap? {
        val safeX = x.coerceAtLeast(0)
        val safeY = y.coerceAtLeast(0)
        val cropWidth = width.coerceAtMost(bitmap.width - safeX)
        val cropHeight = height.coerceAtMost(bitmap.height - safeY)
        if (cropWidth <= 0 || cropHeight <= 0) return null
        if (safeX == 0 && safeY == 0 && cropWidth == bitmap.width && cropHeight == bitmap.height) return bitmap
        return Bitmap.createBitmap(bitmap, safeX, safeY, cropWidth, cropHeight)
    }

    fun padToSquare(bitmap: Bitmap): Bitmap {
        if (bitmap.width == bitmap.height) return bitmap
        val side = maxOf(bitmap.width, bitmap.height)
        val xOffset = (side - bitmap.width) / 2f
        val yOffset = (side - bitmap.height) / 2f
        val square = createBitmap(side, side)
        Canvas(square).apply {
            drawColor(Color.BLACK)
            drawBitmap(bitmap, xOffset, yOffset, null)
        }
        return square
    }

    fun resizeToMinSideAndCenterCrop(bitmap: Bitmap, minSide: Int, cropWidth: Int, cropHeight: Int): Bitmap {
        val scale = if (bitmap.width < bitmap.height) {
            minSide.toFloat() / bitmap.width
        } else {
            minSide.toFloat() / bitmap.height
        }
        val newWidth = (bitmap.width * scale).roundToInt()
        val newHeight = (bitmap.height * scale).roundToInt()
        val xMinResized = (newWidth - cropWidth) shr 1
        val yMinResized = (newHeight - cropHeight) shr 1
        val out = createBitmap(cropWidth, cropHeight)
        val matrix = Matrix().apply {
            postScale(newWidth.toFloat() / bitmap.width, newHeight.toFloat() / bitmap.height)
            postTranslate(-xMinResized.toFloat(), -yMinResized.toFloat())
        }
        Canvas(out).drawBitmap(bitmap, matrix, filterPaint)
        return out
    }
}
