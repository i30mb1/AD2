package n7.ad2.android.extension

import android.widget.ImageView
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