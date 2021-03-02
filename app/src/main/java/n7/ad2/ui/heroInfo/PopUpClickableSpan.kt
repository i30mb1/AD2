package n7.ad2.ui.heroInfo

import android.text.style.ClickableSpan
import android.view.View

internal class PopUpClickableSpan(private val text: String) : ClickableSpan() {

    lateinit var popupListener: ((View, String) -> Unit)

    override fun onClick(widget: View) = popupListener.invoke(widget, text)

}