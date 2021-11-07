package n7.ad2.ui.main

import android.view.MotionEvent

interface TouchEvent {
    var dispatchTouchEvent: ((event: MotionEvent) -> Unit)?
}