package n7.ad2.ktx

import android.content.Context
import android.graphics.Rect
import android.view.TouchDelegate
import android.view.View
import android.view.ViewTreeObserver
import android.view.inputmethod.InputMethodManager
import kotlin.coroutines.resume
import kotlinx.coroutines.suspendCancellableCoroutine

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

/**
 * show keyboard without any problems
 * @link (https://developer.squareup.com/blog/showing-the-android-keyboard-reliably)
 */
fun View.focusAndShowKeyboard(tryAgain: Boolean = true) {
    /**
     * This is to be called when the window already has focus.
     */
    fun View.showTheKeyboardNow() {
        if (isFocused) {
            post {
                // We still post the call, just in case we are being notified of the windows focus
                // but InputMethodManager didn't get properly setup yet.
                val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                val isInputMethodManagerSetup = imm.showSoftInput(this, InputMethodManager.SHOW_IMPLICIT)
                if (!isInputMethodManagerSetup) {
                    // InputMethodManager still didn't get properly setup yet even we post the call.
                    // so we should give it one more try.
                    if (tryAgain) {
                        this.focusAndShowKeyboard(false)
                    }
                }
            }
        }
    }

    requestFocus()
    if (hasWindowFocus()) {
        // No need to wait for the window to get focus.
        showTheKeyboardNow()
    } else {
        // We need to wait until the window gets focus.
        viewTreeObserver.addOnWindowFocusChangeListener(
            object : ViewTreeObserver.OnWindowFocusChangeListener {
                override fun onWindowFocusChanged(hasFocus: Boolean) {
                    // This notification will arrive just before the InputMethodManager gets set up.
                    if (hasFocus) {
                        this@focusAndShowKeyboard.showTheKeyboardNow()
                        // It’s very important to remove this listener once we are done.
                        viewTreeObserver.removeOnWindowFocusChangeListener(this)
                    }
                }
            })
    }
}
