package n7.ad2

import android.view.MotionEvent
import androidx.databinding.BaseObservable
import androidx.databinding.ObservableArrayList

class FingerCoordinate(
    val movementListX: ObservableArrayList<Float> = ObservableArrayList<Float>(),
    val movementListY: ObservableArrayList<Float> = ObservableArrayList<Float>(),
) : BaseObservable() {

    init {
        movementListX.addAll(arrayOfNulls(10))
        movementListY.addAll(arrayOfNulls(10))
    }

    fun dispatchTouchEvent(event: MotionEvent): Boolean {
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
        return false
    }

}