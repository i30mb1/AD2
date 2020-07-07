package n7.ad2.utils

import android.graphics.drawable.Drawable
import android.text.method.LinkMovementMethod
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

@Suppress("UNCHECKED_CAST")
@BindingAdapter("data")
fun <T> RecyclerView.loadData(list: List<T>?) {
    (this.adapter as ListAdapter<T, RecyclerView.ViewHolder>).submitList(list)
}

@BindingAdapter("isVisible", "withSpace", requireAll = false)
fun View.isVisible(isVisible: Boolean, withSpace: Boolean) {
    when (withSpace) {
        true -> this.visibility = if (isVisible) View.VISIBLE else View.INVISIBLE
        false -> this.visibility = if (isVisible) View.VISIBLE else View.GONE
    }

}

@BindingAdapter("isSelected")
fun View.isSelected(isSelected: Boolean) {
    this.isSelected = isSelected
}

@BindingAdapter("android:src")
fun setImageDrawable(view: ImageView, drawable: Int) {
    view.setImageResource(drawable)
}

@BindingAdapter("android:text")
fun setText(view: TextView, text: Int) {
    view.text = text.toString()
}

@BindingAdapter("asyncText", "android:textSize", requireAll = false)
fun TextView.asyncText(text: CharSequence, textSize: Int?) {
    // first, set all measurement affecting properties of the text
    // (size, locale, typeface, direction, etc)
    if (textSize != null) {
        // interpret the text size as SP
        this.textSize = textSize.toFloat()
    }

    val params = TextViewCompat.getTextMetricsParams(this)
    (this as AppCompatTextView).setTextFuture(PrecomputedTextCompat.getTextFuture(text, params, null))
}

@BindingAdapter("clickableText")
fun TextView.clickableText(text: CharSequence?) {
    movementMethod = LinkMovementMethod.getInstance()
    setText(text)
}