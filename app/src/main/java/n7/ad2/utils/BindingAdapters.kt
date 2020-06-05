package n7.ad2.utils

import android.graphics.drawable.Drawable
import android.text.Spannable
import android.text.method.LinkMovementMethod
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
import n7.ad2.R
import n7.ad2.ui.heroInfo.domain.usecase.GetVOHeroDescriptionUseCase.Companion.SEPARATOR
import n7.ad2.utils.extension.themeColor

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

@BindingAdapter("asyncText", "android:textSize", "withDash", requireAll = false)
fun TextView.asyncText(text: CharSequence, textSize: Int?, withDash: Boolean = false) {
    // first, set all measurement affecting properties of the text
    // (size, locale, typeface, direction, etc)
    if (textSize != null) {
        // interpret the text size as SP
        this.textSize = textSize.toFloat()
    }

    val spannable = text.toSpannable()
    if (false) coloringDash(spannable, 0, this.context.themeColor(R.attr.colorAccent))

    val params = TextViewCompat.getTextMetricsParams(this)
    (this as AppCompatTextView).setTextFuture(PrecomputedTextCompat.getTextFuture(spannable, params, null))
}

@BindingAdapter("clickableText")
fun TextView.clickableText(text: CharSequence?) {
    movementMethod = LinkMovementMethod.getInstance()
    setText(text)
}

fun coloringDash(text: Spannable, index: Int, color: Int) {
    val innerIndex = text.indexOf(SEPARATOR, index)
    if (innerIndex == -1) return
    text[innerIndex, innerIndex + SEPARATOR.length] = ForegroundColorSpan(color)
    coloringDash(text, index + 1, color)
}