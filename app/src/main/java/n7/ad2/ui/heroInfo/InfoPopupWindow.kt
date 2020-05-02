package n7.ad2.ui.heroInfo

import android.graphics.Point
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.PopupWindow
import androidx.core.view.doOnPreDraw
import n7.ad2.R

class InfoPopupWindow(private val anchor: View) {

    private val root: View = LayoutInflater.from(anchor.context).inflate(R.layout.popup_window, null)
    private val vPointer: View = root.findViewById(R.id.arrow)
    private val vClose: View = root.findViewById(R.id.close)
    private val popup: PopupWindow

    init {
        popup = PopupWindow(root, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        popup.isOutsideTouchable = true

        root.doOnPreDraw {
            updatePointerLocation()
        }
        vClose.setOnClickListener {
            popup.dismiss()
        }

        popup.showAsDropDown(anchor, 0, 0, Gravity.BOTTOM)
        root.requestLayout()
    }

    fun dismiss() {
        popup.dismiss()
    }

    private fun updatePointerLocation() {
        val locationOnScreen = getLocationOnScreen()

        val params = vPointer.layoutParams as ViewGroup.MarginLayoutParams
        params.leftMargin = locationOnScreen.x
        vPointer.layoutParams = params

    }

    private fun getLocationOnScreen(): Point {
        val location = IntArray(2)
        anchor.getLocationOnScreen(location)
        return Point(location[0] + anchor.pivotX.toInt() - vPointer.width / 2, location[1])
    }

}