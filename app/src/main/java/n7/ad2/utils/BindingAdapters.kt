package n7.ad2.utils

import android.graphics.drawable.Drawable
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.text.PrecomputedTextCompat
import androidx.core.view.isVisible
import androidx.core.widget.TextViewCompat
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import n7.ad2.ui.heroResponse.ResponsesImagesAdapter
import n7.ad2.ui.heroResponse.domain.vo.VOResponseImage

@BindingAdapter("loadImageUrl", "onError", "placeHolder", requireAll = false)
fun ImageView.loadImageUrl(url: String?, error: Drawable?, placeHolder: Drawable?) {
    this.load(url) {
        crossfade(true)
        error(error)
        placeholder(placeHolder)
    }
}

@BindingAdapter("icons")
fun RecyclerView.loadIcons(list: List<VOResponseImage>) {
    (this.adapter as ResponsesImagesAdapter).list = list
}

@Suppress("UNCHECKED_CAST")
@BindingAdapter("data")
fun <T> RecyclerView.loadData(list: List<T>?) {
//    (this.adapter as ListAdapter<T, RecyclerView.ViewHolder>).submitList(list)
}

@BindingAdapter("isVisible", "withSpace", requireAll = false)
fun View.isVisible(isVisible: Boolean, withSpace: Boolean) {
    when (withSpace) {
        true -> this.visibility = if (isVisible) View.VISIBLE else View.INVISIBLE
        false -> this.visibility = if (isVisible) View.VISIBLE else View.GONE
    }

}

@BindingAdapter("android:visibility")
fun View.setVisibility(newState: Boolean) {
    isVisible = newState
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
fun TextView.asyncText(text: CharSequence?, textSize: Int?) {
    if (text == null) return
    if (this.text == text) return
    // first, set all measurement affecting properties of the text
    // (size, locale, typeface, direction, etc)
    if (textSize != null) {
        // interpret the text size as SP
        this.textSize = textSize.toFloat()
    }
    val params = TextViewCompat.getTextMetricsParams(this)
    (this as AppCompatTextView).setTextFuture(PrecomputedTextCompat.getTextFuture(text, params, null))
}