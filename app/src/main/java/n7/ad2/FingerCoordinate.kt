package n7.ad2

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MotionEvent
import android.widget.LinearLayout
import n7.ad2.databinding.FingerCoordinateBinding

class FingerCoordinate(context: Context, attrs: AttributeSet) : LinearLayout(context, attrs) {

    private val coordinatesXY = ArrayList<String>().apply { repeat(10) { add("") } }
    private var binding: FingerCoordinateBinding = FingerCoordinateBinding.inflate(LayoutInflater.from(context), this, true)
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
            MotionEvent.ACTION_UP, MotionEvent.ACTION_POINTER_UP, MotionEvent.ACTION_CANCEL -> coordinatesXY[pointerID] = ""
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
        binding.coordinates = coordinatesXY
    }

    private fun setCoordinateForPoint(pointerID: Int, y: Float, x: Float) {
        builder.clear()
            .append("x[")
            .append(x.toInt())
            .append("]y[")
            .append(y.toInt())
            .append("]")

        coordinatesXY[pointerID] = builder.toString()
    }

}