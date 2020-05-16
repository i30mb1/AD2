package n7.ad2.utils

import android.graphics.drawable.Drawable
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.text.PrecomputedTextCompat
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

@BindingAdapter("data")
fun <T> RecyclerView.loadData(list: List<T>?) {
    (this.adapter as ListAdapter<T, RecyclerView.ViewHolder>).submitList(list)
}

@BindingAdapter("isVisible")
fun View.isVisible(isVisible: Boolean) {
    this.visibility = if (isVisible) View.VISIBLE else View.GONE
}

@BindingAdapter("asyncText", "android:textSize", "withDash", requireAll = false)
fun asyncText(textView: TextView, text: CharSequence, textSize: Int?, withDash: Boolean) {
    // first, set all measurement affecting properties of the text
    // (size, locale, typeface, direction, etc)
    if (textSize != null) {
        // interpret the text size as SP
        textView.textSize = textSize.toFloat()
    }
    val params = TextViewCompat.getTextMetricsParams(textView)
    (textView as AppCompatTextView).setTextFuture(
            PrecomputedTextCompat.getTextFuture(
                    text,
                    params,
                    null
            )
    )
}