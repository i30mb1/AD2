package n7.ad2.span_parser

import android.graphics.LinearGradient
import android.graphics.Shader
import android.text.Spanned
import android.text.TextPaint
import android.text.style.CharacterStyle
import android.text.style.UpdateAppearance
import androidx.annotation.ColorInt

internal class LinearGradientSpan(
    private val text: Spanned,
    @ColorInt private val startColorInt: Int,
    @ColorInt private val endColorInt: Int,
) : CharacterStyle(), UpdateAppearance {


    override fun updateDrawState(tp: TextPaint) {
        val gradientWidth = tp.measureText(text, 0, text.length)
        tp.shader = LinearGradient(0f, 0f, gradientWidth, 0f, startColorInt, endColorInt, Shader.TileMode.REPEAT)
    }

}