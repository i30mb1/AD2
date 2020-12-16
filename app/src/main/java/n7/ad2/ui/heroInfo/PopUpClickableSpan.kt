package n7.ad2.ui.heroInfo

import android.text.style.ClickableSpan
import android.view.View
import n7.ad2.base.VOPopUpListener

internal class PopUpClickableSpan(private val text: String) : ClickableSpan() {

    var popUpListener: VOPopUpListener<String>? = null

    override fun onClick(widget: View) {
        popUpListener?.onClickListener(widget, text)
    }
}