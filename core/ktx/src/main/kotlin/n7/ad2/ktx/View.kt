package n7.ad2.ktx

import android.graphics.Rect
import android.view.TouchDelegate
import android.view.View
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

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

// https://chris.banes.dev/suspending-views/
suspend fun View.awaitNextLayout() = suspendCancellableCoroutine<Unit> { continuation ->
    val listener = object : View.OnLayoutChangeListener {
        override fun onLayoutChange(view: View, left: Int, top: Int, right: Int, bottom: Int, oldLeft: Int, oldTop: Int, oldRight: Int, oldBottom: Int) {
            view.removeOnLayoutChangeListener(this)
            continuation.resume(Unit)
        }
    }
    continuation.invokeOnCancellation { removeOnLayoutChangeListener(listener) }
    addOnLayoutChangeListener(listener)
}