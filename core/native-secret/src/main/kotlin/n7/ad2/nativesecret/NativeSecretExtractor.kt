package n7.ad2.nativesecret

import android.graphics.Bitmap

class NativeSecretExtractor {

    private var nativeHandle: Long = 0

    init {
        System.loadLibrary("native")
        nativeHandle = createNative()
    }

    external fun printHelloWorld(): String

    /**
     * Resize an image.
     *
     * Resizes an image using bicubic interpolation.
     *
     * This method supports input Bitmap of config ARGB_8888 and ALPHA_8. The returned Bitmap
     * has the same config. Bitmaps with a stride different than width * vectorSize are not
     * currently supported.
     *
     * An optional range parameter can be set to restrict the operation to a rectangular subset
     * of the output buffer. The corresponding scaled range of the input will be used. If provided,
     * the range must be wholly contained with the dimensions described by outputSizeX and
     * outputSizeY.
     *
     * @param inputBitmap The Bitmap to be resized.
     * @param outputSizeX The width of the output buffer, as a number of 1-4 byte elements.
     * @param outputSizeY The height of the output buffer, as a number of 1-4 byte elements.
     * @return A Bitmap that contains the rescaled image.
     */
    fun resize(
        inputBitmap: Bitmap,
        outputSizeX: Int,
        outputSizeY: Int,
    ): Bitmap {
        val outputBitmap = Bitmap.createBitmap(outputSizeX, outputSizeY, Bitmap.Config.ARGB_8888)
        nativeResizeBitmap(nativeHandle, inputBitmap, outputBitmap)
        return outputBitmap
    }

    private external fun nativeResizeBitmap(
        nativeHandle: Long,
        inputBitmap: Bitmap,
        outputBitmap: Bitmap,
    )

    private external fun createNative(): Long
}
