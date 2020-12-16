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
fun String.spanWithDotaImages(application: Application): SpannableString {
    val tagTalent = "TagTalent"
    var startIndex = 0
    var indexOf = this.indexOf(tagTalent, startIndex)
    val spannableString = SpannableString(this)
    if (indexOf == -1) return spannableString
    val icon = application.resources.getDrawable(R.drawable.talent, null)
    while (indexOf != -1) {
        spannableString[indexOf..indexOf + tagTalent.length] = ImageSpan(icon, DynamicDrawableSpan.ALIGN_BOTTOM)
        spannableString[indexOf..indexOf + tagTalent.length] = PopUpClickableSpan(application.getString(R.string.info_talent))
        indexOf = this.indexOf(tagTalent, ++startIndex)
    }
    return spannableString
}