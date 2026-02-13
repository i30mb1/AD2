package n7.ad2.android

import android.view.MotionEvent

interface TouchEvent {
    var dispatchTouchEvent: ((event: MotionEvent) -> Unit)?
}
