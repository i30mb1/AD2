package n7.ad2.ui.heroInfo

import android.content.Context
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
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import n7.ad2.databinding.PopupSpellInfoBinding

class InfoPopupWindow(
        context: Context,
        private val lifecycle: Lifecycle
) : LifecycleObserver {

    private val binding = PopupSpellInfoBinding.inflate(LayoutInflater.from(context))
    private val popup: PopupWindow

    init {
        lifecycle.addObserver(this)
        popup = PopupWindow(binding.root, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
            isOutsideTouchable = true
        }

        binding.body.setOnClickListener { popup.dismiss() }
    }

    fun show(anchor: View, text: String) {
        binding.body.text = text
        popup.showAsDropDown(anchor, 0, 0, Gravity.BOTTOM)
        binding.root.doOnPreDraw { updatePointerLocation(anchor) }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    private fun dismiss() = popup.dismiss()

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    private fun onDestroy() = lifecycle.removeObserver(this)

    private fun updatePointerLocation(anchor: View) {
        val (x, _) = getLocationOnScreen(anchor)

        val params = binding.pointer.layoutParams as ViewGroup.MarginLayoutParams
        params.leftMargin = x
        binding.pointer.layoutParams = params
    }

    private fun getLocationOnScreen(anchor: View): Point {
        val location = IntArray(2)
        anchor.getLocationOnScreen(location)
        return Point(location[0] + anchor.pivotX.toInt() - binding.pointer.width / 2, location[1])
    }

}