package n7.ad2.android.extension

import android.widget.ImageView
import coil.dispose
import coil.load

fun ImageView.load(url: String, placeHolder: Int? = null, errorPlaceHolder: Int? = null) {
    load(url) {
        crossfade(1000)
        if (placeHolder != null) placeholder(placeHolder)
        if (errorPlaceHolder != null) error(errorPlaceHolder)
    }
}

fun ImageView.load(url: Int, placeHolder: Int? = null, errorPlaceHolder: Int? = null) {
    load(url) {
        crossfade(1000)
        if (placeHolder != null) placeholder(placeHolder)
        if (errorPlaceHolder != null) error(errorPlaceHolder)
    }
}

fun ImageView.clear() {
    dispose()
}