package ad2.n7.android.extension

import android.widget.ImageView
import coil.clear
import coil.load

fun ImageView.load(url: String, placeHolder: Int? = null) {
    load(url) {
        crossfade(1000)
        if (placeHolder != null) placeholder(placeHolder)
    }
}

fun ImageView.clear() {
    clear()
}