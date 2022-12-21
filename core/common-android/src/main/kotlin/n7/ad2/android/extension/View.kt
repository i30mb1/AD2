package n7.ad2.android.extension

import android.view.View
import com.google.android.material.snackbar.Snackbar

fun View.showSnackbar(snackbarText: String, timeLength: Int = Snackbar.LENGTH_SHORT, actionText: String? = null, action: (() -> Unit)? = null) {
    Snackbar.make(this, snackbarText, timeLength).run {
        addCallback(object : Snackbar.Callback() {
            override fun onShown(sb: Snackbar?) {
                // EspressoIdlingResource.increment()
            }

            override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                // EspressoIdlingResource.decrement()
            }
        })
        if (actionText != null) setAction(actionText) {
            action?.invoke()
            dismiss()
        }
        show()
    }
}