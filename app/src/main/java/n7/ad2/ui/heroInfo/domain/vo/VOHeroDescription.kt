package n7.ad2.ui.heroInfo.domain.vo

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import n7.ad2.BR

class VOHeroDescription : BaseObservable() {

    @get:Bindable
    var heroImagePath: String = ""
    set(value) {
        field = value
        notifyPropertyChanged(BR.heroImagePath)
    }

    @get:Bindable
    var spells: List<VOSpell> = emptyList()
    set(value) {
        field = value
        notifyPropertyChanged(BR.spells)
    }

    @get:Bindable
    var selectedDescriptionList: List<VOHeroInfo> = emptyList()
        set(value) {
            field = value
            notifyPropertyChanged(BR.selectedDescriptionList)
        }

    @get:Bindable
    var heroBio: List<VOHeroInfo> = emptyList()
        set(value) {
            field = value
            notifyPropertyChanged(BR.heroBio)
        }

}