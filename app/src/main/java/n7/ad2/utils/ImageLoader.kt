package n7.ad2.utils

import android.widget.ImageView
import coil.clear
import coil.load
import javax.inject.Inject

class ImageLoader @Inject constructor() {

    fun load(imageView: ImageView, url: String, placeholder: Int) {
        imageView.load(url) {
            crossfade(300)
            placeholder(placeholder)
        }
    }

    fun clear(imageView: ImageView) {
        imageView.clear()
    }

}