package n7.ad2.utils.extension

import android.annotation.SuppressLint
import android.app.Application
import android.text.SpannableString
import android.text.style.DynamicDrawableSpan
import android.text.style.ImageSpan
import androidx.core.text.set
import n7.ad2.R
import n7.ad2.ui.heroInfo.PopUpClickableSpan

@SuppressLint("UseCompatLoadingForDrawables")
fun String.spanWithDotaImages(application: Application, clickable: Boolean = false): SpannableString {
    data class DotaImage(val tag: String, val tip: Int, val icon: Int)
    val list = listOf(
        DotaImage("TagTalent", R.string.popup_talent, R.drawable.talent),
        DotaImage("TagAghanim", R.string.popup_aghanim, R.drawable.aghanim),
    )
    val spannableString = SpannableString(this)
    list.forEach { (tag, tip, drawable) ->
        val icon = application.resources.getDrawable(drawable, null)
        var startIndex = 0
        var indexOf = this.indexOf(tag, startIndex)

        while (indexOf != -1) {
            spannableString[indexOf..indexOf + tag.length] = ImageSpan(icon, DynamicDrawableSpan.ALIGN_BOTTOM)
           if(clickable) spannableString[indexOf..indexOf + tag.length] = PopUpClickableSpan(application.getString(tip))
            indexOf = this.indexOf(tag, ++startIndex)
        }
    }

    return spannableString
}