package n7.ad2.ui.heroes.domain.vo

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import n7.ad2.BR

class VOHero : BaseObservable() {

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
    var viewedByUser: Boolean = false
    set(value) {
        field = value
        notifyPropertyChanged(BR.viewedByUser)
    }

}