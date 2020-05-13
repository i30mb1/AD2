package n7.ad2.utils

import android.graphics.drawable.Drawable
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.api.load
import n7.ad2.ui.heroInfo.domain.vo.VOSpell

@BindingAdapter("loadImageUrl", "onError", "placeHolder", requireAll = false)
fun ImageView.loadImageUrl(url: String?, error: Drawable?, placeHolder: Drawable?) {
    this.load(url) {
        crossfade(true)
        error(error)
        placeholder(placeHolder)
    }
}

@BindingAdapter("data")
fun <T> RecyclerView.loadData(list: List<T>?) {
    (this.adapter as ListAdapter<T, RecyclerView.ViewHolder>).submitList(list)
}