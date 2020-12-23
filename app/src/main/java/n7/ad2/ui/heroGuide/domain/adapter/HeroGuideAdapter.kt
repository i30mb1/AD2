package n7.ad2.ui.heroGuide.domain.adapter

import android.content.Context
import android.view.View
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import coil.load
import n7.ad2.data.source.local.Repository
import n7.ad2.utils.extension.toPx

fun List<String>.toVOHardToWinHeroes(context: Context): List<ImageView> = this.map { heroName ->
    ImageView(context).apply {
        layoutParams = ConstraintLayout.LayoutParams(70.toPx, 40.toPx)
        id = View.generateViewId()
        load(Repository.getFullUrlHeroImage(heroName))
    }
}

fun List<String>.toVOEasyToWinHeroes(context: Context): List<ImageView> = this.map { heroName ->
    ImageView(context).apply {
        layoutParams = ConstraintLayout.LayoutParams(70.toPx, 40.toPx)
        id = View.generateViewId()
        load(Repository.getFullUrlHeroImage(heroName))
    }
}
