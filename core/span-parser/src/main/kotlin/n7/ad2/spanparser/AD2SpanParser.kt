package n7.ad2.spanparser

import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.BackgroundColorSpan
import android.text.style.DynamicDrawableSpan
import android.text.style.ForegroundColorSpan
import android.text.style.ImageSpan
import android.text.style.StrikethroughSpan
import android.text.style.StyleSpan
import android.text.style.UnderlineSpan
import androidx.annotation.VisibleForTesting
import androidx.core.text.set
import androidx.core.text.toSpannable
import n7.ad2.Resources
import javax.inject.Inject

internal data class AttributeAndValue(val attribute: String, val value: String)

internal class AD2SpanParser @Inject constructor(
    private val res: Resources,
) : SpanParser {

    companion object {
        private const val START_TAG_SPAN_OPEN = "<span "
        private const val START_TAG_SPAN_CLOSE = '>'
        private const val END_TAG_SPAN = "</span>"
        private const val ATTRIBUTE_DELIMITER = '='
        private const val ATTRIBUTE_AND_VALUE_DELIMITER = ' '
        private const val SKIP_ATTRS_SIGN = '"'
    }

    override fun toSpannable(string: String, isNightTheme: Boolean): Spannable {
        val iterator = string.iterator()
        val result = SpannableStringBuilder()
        val attributeAndValue: ArrayDeque<List<AttributeAndValue>> = ArrayDeque()
        analyzer(iterator) { span: Analyzer ->
            when (span) {
                is StartSpanTag -> {
                    result.append(applyAttributesToText(span.text, attributeAndValue.flatten(), isNightTheme))
                    attributeAndValue.addFirst(span.attributes)
                }
                is EndSpanTag -> {
                    result.append(applyAttributesToText(span.text, attributeAndValue.flatten(), isNightTheme))
                    attributeAndValue.removeFirstOrNull()
                }
                is RemainingText -> result.append(span.text)
            }
        }
        return result
    }

    @VisibleForTesting
    fun analyzer(iterator: CharIterator, callback: (span: Analyzer) -> Unit) {
        val bufferText = StringBuilder()
        while (iterator.hasNext()) {
            bufferText.append(iterator.nextChar())
            when {
                bufferText.endsWith(START_TAG_SPAN_OPEN) -> {
                    val attributes = getAttributesWithValueInSpan(iterator)
                    val text = bufferText.toString().dropLast(START_TAG_SPAN_OPEN.length)
                    callback(StartSpanTag(text, attributes))
                    bufferText.clear()
                }
                bufferText.endsWith(END_TAG_SPAN) -> {
                    val text = bufferText.toString().dropLast(END_TAG_SPAN.length)
                    callback(EndSpanTag(text))
                    bufferText.clear()
                }
            }
        }
        if (bufferText.isNotEmpty()) callback(RemainingText(bufferText.toString()))
    }

    private fun applyAttributesToText(
        bufferText: String,
        attributeAndValueList: List<AttributeAndValue>,
        isNightTheme: Boolean,
    ): Spannable {
        val result = bufferText.toSpannable()
        val uniqueAttributeAndValueList = attributeAndValueList.distinctBy { it.attribute }
        for ((attribute, value) in uniqueAttributeAndValueList) when (attribute) {
            "color" -> if (!isNightTheme) result[0, result.length] = setForegroundColorSpan(value, result)
            "colorNight" -> if (isNightTheme) result[0, result.length] = setForegroundColorSpan(value, result)
            "background" -> if (!isNightTheme) result[0, result.length] = setBackgroundColorSpan(value, result)
            "backgroundNight" -> if (isNightTheme) result[0, result.length] = setBackgroundColorSpan(value, result)
            "linearGradient" -> result[0, result.length] = setLinearGradient(value, result)
            "underline" -> result[0, result.length] = UnderlineSpan()
            "image" -> {
                val parts = value.split(".")
                val path = parts[0] + ".${parts[1]}"
                val pathNight = (if (isNightTheme) "${parts[0]}-night" else parts[0]) + ".${parts[1]}"
                val drawable = runCatching { Drawable.createFromStream(res.getAssets(pathNight), null) }.getOrNull()
                    ?: runCatching { Drawable.createFromStream(res.getAssets(path), null) }.getOrNull()
                if (drawable != null) result[0, result.length] = ImageSpan(drawable, DynamicDrawableSpan.ALIGN_BOTTOM)
            }
            "click" -> result[0, result.length] = AD2ClickableSpan(AD2ClickableSpan.Data(value, bufferText))
            "style" -> when (value) {
                "strike" -> result[0, result.length] = StrikethroughSpan()
                "bold" -> result[0, result.length] = StyleSpan(Typeface.BOLD)
                "italic" -> result[0, result.length] = StyleSpan(Typeface.ITALIC)
            }
        }
        return result
    }

    private fun parseColor(color: String): Int? {
        return try {
            Color.parseColor(color)
        } catch (e: Exception) {
            null
        }
    }

    private fun setBackgroundColorSpan(value: String, result: Spannable) {
        val color = parseColor(value) ?: return
        result[0, result.length] = BackgroundColorSpan(color)
    }

    private fun setForegroundColorSpan(value: String, result: Spannable) {
        val color = parseColor(value) ?: return
        result[0, result.length] = ForegroundColorSpan(color)
    }

    private fun setLinearGradient(value: String, result: Spannable) {
        val (from, to) = value.split("to")
        val colorFrom = parseColor(from) ?: return
        val colorTo = parseColor(to) ?: return
        result[0, result.length] = LinearGradientSpan(result, colorFrom, colorTo)
    }

    private fun getAttributesWithValueInSpan(iterator: CharIterator): List<AttributeAndValue> {
        val bufferText = StringBuilder()
        while (iterator.hasNext()) {
            val char = iterator.nextChar()
            if (char == START_TAG_SPAN_CLOSE) {
                return bufferText.split(ATTRIBUTE_AND_VALUE_DELIMITER).mapNotNull {
                    val attributeAndValue = it.split(ATTRIBUTE_DELIMITER, limit = 2)

                    if (attributeAndValue.size == 2) {
                        val attribute = attributeAndValue[0]
                        val value = attributeAndValue[1]
                        if (value.isEmpty() || attribute.isEmpty()) return@mapNotNull null
                        AttributeAndValue(attribute, value)
                    } else null
                }
            }
            if (char != SKIP_ATTRS_SIGN) bufferText.append(char)
        }
        return emptyList()
    }

}