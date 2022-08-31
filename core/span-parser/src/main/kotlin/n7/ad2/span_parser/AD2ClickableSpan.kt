package n7.ad2.span_parser

import android.text.TextPaint
import android.text.style.ClickableSpan
import android.view.View

class AD2ClickableSpan(private val data: Data) : ClickableSpan() {
    data class Data(val tag: String, val value: String)

    val listener: ((Data) -> Unit)? = null
    override fun onClick(widget: View) = listener?.invoke(data) ?: Unit
    override fun updateDrawState(ds: TextPaint) = Unit
}