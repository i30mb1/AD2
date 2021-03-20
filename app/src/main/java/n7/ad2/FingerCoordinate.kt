package n7.ad2

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MotionEvent
import android.widget.LinearLayout
import androidx.databinding.ObservableArrayList
import n7.ad2.databinding.FingerCoordinateBinding

class FingerCoordinate(context: Context, attrs: AttributeSet) : LinearLayout(context, attrs) {

    private val movementListX: ObservableArrayList<Float> = ObservableArrayList<Float>()
    private val movementListY: ObservableArrayList<Float> = ObservableArrayList<Float>()
    private var binding: FingerCoordinateBinding = FingerCoordinateBinding.inflate(LayoutInflater.from(context), this, true)

    init {
        orientation = VERTICAL
        movementListX.addAll(arrayOfNulls(10))
        movementListY.addAll(arrayOfNulls(10))
    }

    fun handleGlobalEvent(event: MotionEvent) {
        val action = event.actionMasked
        var index = event.actionIndex
        var pointerID = event.getPointerId(index)
        when (action) {
            MotionEvent.ACTION_DOWN, MotionEvent.ACTION_POINTER_DOWN -> {
                movementListX[pointerID] = event.getX(index)
                movementListY[pointerID] = event.getY(index)
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_POINTER_UP, MotionEvent.ACTION_CANCEL -> {
                movementListX[pointerID] = 0F
                movementListY[pointerID] = 0F
            }
            MotionEvent.ACTION_MOVE -> {
                val pointerCount = event.pointerCount
                var i = 0
                while (i < pointerCount) {
                    index = i
                    pointerID = event.getPointerId(index)
                    movementListX[pointerID] = event.getX(index)
                    movementListY[pointerID] = event.getY(index)
                    i++
                }
            }
        }
        binding.arrayX = movementListX
        binding.arrayY = movementListY
    }

}