package n7.ad2.ui.heroInfo

import android.text.style.ClickableSpan
import android.view.View

internal class PopUpClickableSpan(private val text: String) : ClickableSpan() {

    var popUpListener: ((View, String) -> Unit)? = null

    override fun onClick(widget: View) {
        popUpListener?.invoke(widget, text)
    }
}