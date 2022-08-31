package n7.ad2.span_parser

import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.BackgroundColorSpan
import android.text.style.ClickableSpan
import android.text.style.DynamicDrawableSpan
import android.text.style.ForegroundColorSpan
import android.text.style.ImageSpan
import android.text.style.StrikethroughSpan
import android.text.style.StyleSpan
import android.text.style.UnderlineSpan
import android.view.View
import androidx.annotation.VisibleForTesting
import androidx.core.text.set
import androidx.core.text.toSpannable
import n7.ad2.Resources
import javax.inject.Inject

class AD2ClickableSpan(private val data: Data) : ClickableSpan() {

    data class Data(val tag: String, val value: String)

    val listener: ((Data) -> Unit)? = null
    override fun onClick(widget: View) = listener?.invoke(data) ?: Unit
}

sealed interface Analyzer
data class StartSpanTag(val text: String, val attributes: List<AttributeAndValue>) : Analyzer
data class EndSpanTag(val text: String) : Analyzer
data class RemainingText(val text: String) : Analyzer

data class AttributeAndValue(val attribute: String, val value: String)

class AD2StringParser @Inject constructor(
    private val res: Resources,
) {

    companion object {
        private const val START_TAG_SPAN_OPEN = "<span "
        private const val START_TAG_SPAN_CLOSE = '>'
        private const val END_TAG_SPAN = "</span>"
        private const val ATTRIBUTE_DELIMITER = '='
        private const val ATTRIBUTE_AND_VALUE_DELIMITER = ' '
        private const val SKIP_ATTRS_SIGN = '"'
    }

    fun toSpannable(
        string: String,
        isNightTheme: Boolean = false,
    ): SpannableStringBuilder {
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
            "color" -> if (!isNightTheme) result[0, result.length] = ForegroundColorSpan(Color.parseColor(value))
            "colorNight" -> if (isNightTheme) result[0, result.length] = ForegroundColorSpan(Color.parseColor(value))
            "background" -> if (!isNightTheme) result[0, result.length] = BackgroundColorSpan(Color.parseColor(value))
            "backgroundNight" -> if (isNightTheme) result[0, result.length] = BackgroundColorSpan(Color.parseColor(value))
            "underline" -> result[0, result.length] = UnderlineSpan()
            "image" -> {
                val drawable = Drawable.createFromStream(res.getAssets(value), null)
                result[0, result.length] = ImageSpan(drawable, DynamicDrawableSpan.ALIGN_BOTTOM)
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

    private fun getAttributesWithValueInSpan(iterator: CharIterator): List<AttributeAndValue> {
        val bufferText = StringBuilder()
        while (iterator.hasNext()) {
            val char = iterator.nextChar()
            if (char == START_TAG_SPAN_CLOSE) {
                return bufferText.split(ATTRIBUTE_AND_VALUE_DELIMITER).mapNotNull {
                    val attributeAndValue = it.split(ATTRIBUTE_DELIMITER)

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