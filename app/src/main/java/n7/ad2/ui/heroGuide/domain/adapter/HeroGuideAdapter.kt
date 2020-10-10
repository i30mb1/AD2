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
    return VOHeroGuide().also {
        it.heroBestVersus = heroBestVersus.map {
            ImageView(context).apply {
                layoutParams = ConstraintLayout.LayoutParams(128, 72)
                id = View.generateViewId()
                load("file:///android_asset/heroes/${it}/${Repository.ASSETS_FILE_FULL}")
            }
        }
        TextView(context, null, R.attr.textAppearanceBody1).apply {
            layoutParams = ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            updatePadding(left = 4.toDp, right = 4.toDp)
            id = View.generateViewId()
            text = context.getText(R.string.best_versus)

            (it.heroBestVersus as MutableList).add(0, this)
        }
    }
}