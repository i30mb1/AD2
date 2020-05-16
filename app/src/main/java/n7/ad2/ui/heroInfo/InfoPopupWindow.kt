package n7.ad2.ui.heroInfo

import android.graphics.Point
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.PopupWindow
import androidx.core.graphics.component1
import androidx.core.graphics.component2
import androidx.core.view.doOnPreDraw
import n7.ad2.databinding.PopupSpellInfoBinding

class InfoPopupWindow(private val anchor: View, private val text: String) {

    private val binding = PopupSpellInfoBinding.inflate(LayoutInflater.from(anchor.context))
    private val popup: PopupWindow

    init {
        popup = PopupWindow(binding.root, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
            isOutsideTouchable = true
            showAsDropDown(anchor, 0, 0, Gravity.BOTTOM)
        }

        binding.root.doOnPreDraw {
            updatePointerLocation()
        }
        binding.body.text = text
        binding.body.setOnClickListener {
            popup.dismiss()
        }

    }

    fun dismiss() {
        popup.dismiss()
    }

    private fun updatePointerLocation() {
        val (x, y) = getLocationOnScreen()

        val params = binding.pointer.layoutParams as ViewGroup.MarginLayoutParams
        params.leftMargin = x
        binding.pointer.layoutParams = params
    }

    private fun getLocationOnScreen(): Point {
        val location = IntArray(2)
        anchor.getLocationOnScreen(location)
        return Point(location[0] + anchor.pivotX.toInt() - binding.pointer.width / 2, location[1])
    }

}