package n7.ad2.ui.heroInfo

import android.text.style.ClickableSpan
import android.view.View

internal class PopUpClickableSpan(private val text: String) : ClickableSpan() {

    var popupListener: ((View, String) -> Unit)? = null

    override fun onClick(widget: View) {
        popupListener?.invoke(widget, text)
    }
}