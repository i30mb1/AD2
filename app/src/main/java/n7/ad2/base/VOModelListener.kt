package n7.ad2.base

import androidx.databinding.BaseObservable

interface VOModelListener<T: BaseObservable> {
    fun onClickListener(model: T)
}

interface VOStringListener {
    fun onClickListener(text: String)
}