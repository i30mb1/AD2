package n7.ad2.ui.heroInfo.domain.vo

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import n7.ad2.BR

data class VOSpell(
    val name: String,
    val urlSpellImage: String,
    val voDescriptionList: List<VOHeroInfo>,
) : BaseObservable() {

    @get:Bindable
    var selected: Boolean = false
        set(value) {
            field = value
            notifyPropertyChanged(BR.selected)
        }

}