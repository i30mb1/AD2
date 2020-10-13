package n7.ad2.ui.heroGuide.domain.adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.updatePadding
import coil.load
import n7.ad2.R
import n7.ad2.data.source.local.Repository
import n7.ad2.ui.heroGuide.domain.model.LocalGuideJson
import n7.ad2.ui.heroGuide.domain.vo.VOHeroGuide
import n7.ad2.utils.extension.toDp

fun LocalGuideJson.toVO(context: Context): VOHeroGuide {
    val best = mutableListOf<View>()
    best.add(inflateDescriptionTextView(context, R.string.best_versus))
    best.addAll(heroBestVersus.mapToIcons(context))

    val worst = mutableListOf<View>()
    worst.add(inflateDescriptionTextView(context, R.string.worst_versus))
    worst.addAll(heroWorstVersus.mapToIcons(context))

    return VOHeroGuide(best, worst)
}


private fun inflateDescriptionTextView(context: Context, textId: Int) = TextView(context, null, R.attr.textAppearanceBody1).apply {
    layoutParams = ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    updatePadding(left = 4.toDp, right = 4.toDp)
    id = View.generateViewId()
    text = context.getText(textId)
}


private fun List<String>.mapToIcons(context: Context): List<ImageView> = this.map {
    ImageView(context).apply {
        layoutParams = ConstraintLayout.LayoutParams(128, 72)
        id = View.generateViewId()
        load("file:///android_asset/heroes/${it}/${Repository.ASSETS_FILE_FULL}")
    }
}
