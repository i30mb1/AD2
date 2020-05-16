package n7.ad2.utils

import android.graphics.Color
import android.graphics.drawable.Drawable
import android.text.style.ForegroundColorSpan
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.text.PrecomputedTextCompat
import androidx.core.text.set
import androidx.core.text.toSpannable
import androidx.core.widget.TextViewCompat
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.api.load

@BindingAdapter("loadImageUrl", "onError", "placeHolder", requireAll = false)
fun ImageView.loadImageUrl(url: String?, error: Drawable?, placeHolder: Drawable?) {
    this.load(url) {
        crossfade(true)
        error(error)
        placeholder(placeHolder)
    }
}

@Suppress("UNCHECKED_CAST")
@BindingAdapter("data")
fun <T> RecyclerView.loadData(list: List<T>?) {
    (this.adapter as ListAdapter<T, RecyclerView.ViewHolder>).submitList(list)
}

@BindingAdapter("isVisible")
fun View.isVisible(isVisible: Boolean) {
    this.visibility = if (isVisible) View.VISIBLE else View.GONE
}

@BindingAdapter("asyncText", "android:textSize", "withDash", requireAll = false)
fun asyncText(textView: TextView, text: CharSequence, textSize: Int?, withDash: Boolean = false) {
    // first, set all measurement affecting properties of the text
    // (size, locale, typeface, direction, etc)
    if (textSize != null) {
        // interpret the text size as SP
        textView.textSize = textSize.toFloat()
    }

    val spannable = text.toSpannable()
    if (withDash) {
//        val index = spannable.indexOf("-")
//        while (index != -1) {
        spannable[0, 10] = ForegroundColorSpan(Color.RED)
//        }
    }

    val params = TextViewCompat.getTextMetricsParams(textView)
    (textView as AppCompatTextView).setTextFuture(
            PrecomputedTextCompat.getTextFuture(
                    spannable,
                    params,
                    null
            )
    )
}