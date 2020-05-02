package n7.ad2.base

import androidx.databinding.BaseObservable

interface BaseVOListener<T: BaseObservable> {
    fun onClickListener(model: T)
}