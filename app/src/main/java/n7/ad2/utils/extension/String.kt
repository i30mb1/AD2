package n7.ad2.utils.extension

import android.annotation.SuppressLint
import android.app.Application
import android.text.Spannable
import android.text.SpannableString
import android.text.style.DynamicDrawableSpan
import android.text.style.ImageSpan
import n7.ad2.R
import n7.ad2.ui.heroInfo.MyClickableSpan
import n7.ad2.ui.heroInfo.domain.usecase.GetVOHeroDescriptionUseCase

@SuppressLint("UseCompatLoadingForDrawables")
fun String.spanWithDotaImages(application: Application): SpannableString {
    var startIndex = 0
    var indexOf = this.indexOf(GetVOHeroDescriptionUseCase.TAG_TALENT, startIndex)
    val spannableString = SpannableString(this)
    if (indexOf == -1) return spannableString
    val icon = application.resources.getDrawable(R.drawable.talent, null).apply {
        val imageSize = application.resources.getDimensionPixelSize(R.dimen.icon_in_description)
        setBounds(0, 0, imageSize, imageSize)
    }
    do {
        spannableString.setSpan(ImageSpan(icon, DynamicDrawableSpan.ALIGN_BOTTOM), indexOf, indexOf + GetVOHeroDescriptionUseCase.TAG_TALENT.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        spannableString.setSpan(MyClickableSpan(application.getString(R.string.info_talent)), indexOf, indexOf + GetVOHeroDescriptionUseCase.TAG_TALENT.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        startIndex++
        indexOf = this.indexOf(GetVOHeroDescriptionUseCase.TAG_TALENT, startIndex)
    } while (indexOf != -1)

    return spannableString
}