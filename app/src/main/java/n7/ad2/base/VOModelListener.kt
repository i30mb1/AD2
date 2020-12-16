package n7.ad2.base

import android.view.View
import androidx.databinding.BaseObservable

interface VOObjectListener<T> {
    fun onClickListener(any: T)
}

interface VOModelListener<T : BaseObservable> {
    fun onClickListener(model: T)
}

interface VOPopUpListener<T> {
    fun onClickListener(view: View, text: T)
}