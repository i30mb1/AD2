package n7.ad2.ui.heroInfo

import android.text.style.ClickableSpan
import android.view.View
import n7.ad2.base.VOPopUpListener

internal class MyClickableSpan(private val text: String) : ClickableSpan() {

    var listener: VOPopUpListener<String>? = null

    override fun onClick(widget: View) {
        listener?.onClickListener(widget, text)
    }
}