package n7.ad2

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MotionEvent
import android.widget.LinearLayout
import n7.ad2.databinding.FingerCoordinateBinding

class FingerCoordinate(context: Context, attrs: AttributeSet) : LinearLayout(context, attrs) {

    private var binding = FingerCoordinateBinding.inflate(LayoutInflater.from(context), this)
    private val builder = StringBuilder(14)

    init {
        orientation = VERTICAL
    }

    fun handleGlobalEvent(event: MotionEvent) {
        val action = event.actionMasked
        var index = event.actionIndex
        var pointerID = event.getPointerId(index)
        when (action) {
            MotionEvent.ACTION_DOWN, MotionEvent.ACTION_POINTER_DOWN -> setCoordinateForPoint(pointerID, event.getX(index), event.getY(index))
            MotionEvent.ACTION_UP, MotionEvent.ACTION_POINTER_UP, MotionEvent.ACTION_CANCEL -> updateCoordinate(pointerID, "")
            MotionEvent.ACTION_MOVE -> {
                val pointerCount = event.pointerCount
                index = 0
                while (index < pointerCount) {
                    pointerID = event.getPointerId(index)
                    setCoordinateForPoint(pointerID, event.getX(index), event.getY(index))
                    index++
                }
            }
        }
    }

    private fun setCoordinateForPoint(pointerID: Int, y: Float, x: Float) {
        builder.clear()
            .append("x[")
            .append(x.toInt())
            .append("]y[")
            .append(y.toInt())
            .append("]")

        updateCoordinate(pointerID, builder.toString())
    }

    private fun updateCoordinate(pointerID: Int, text: String) {
        val tv = when (pointerID) {
            0 -> binding.tv1
            1 -> binding.tv2
            2 -> binding.tv3
            3 -> binding.tv4
            4 -> binding.tv5
            5 -> binding.tv6
            6 -> binding.tv7
            7 -> binding.tv8
            8 -> binding.tv9
            else -> binding.tv10
        }
        tv.text = text
    }

}