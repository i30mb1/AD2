package n7.ad2.ui.heroGuide.domain.vo

import android.view.View
import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import n7.ad2.BR

class VOHeroGuide : BaseObservable() {

    @get:Bindable
    var heroBestVersus: MutableList<View> = mutableListOf()
        set(value) {
            field = value
            notifyPropertyChanged(BR.heroBestVersus)
        }

}