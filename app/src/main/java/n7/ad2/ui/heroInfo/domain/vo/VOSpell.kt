package n7.ad2.ui.heroInfo.domain.vo

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import n7.ad2.BR

class VOSpell : BaseObservable() {

    @get:Bindable
    var name: String = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.name)
        }

    @get:Bindable
    var image: String = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.image)
        }

    @get:Bindable
    var selected: Boolean = false
        set(value) {
            field = value
            notifyPropertyChanged(BR.selected)
        }

}