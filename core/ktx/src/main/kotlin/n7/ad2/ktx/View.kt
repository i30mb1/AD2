package n7.ad2.ktx

import android.graphics.Rect
import android.view.TouchDelegate
import android.view.View

fun View.setTouchDelegate(rect: Rect) {
    post {
        val delegateArea = Rect()
        getHitRect(delegateArea)
        delegateArea.set(
            delegateArea.left - rect.left.dpToPx,
            delegateArea.top - rect.top.dpToPx,
            delegateArea.right + rect.right.dpToPx,
            delegateArea.bottom + rect.bottom.dpToPx
        )
        (parent as View).touchDelegate = TouchDelegate(delegateArea, this)
    }
}