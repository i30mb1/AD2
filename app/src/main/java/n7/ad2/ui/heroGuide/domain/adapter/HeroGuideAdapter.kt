package n7.ad2.ui.heroGuide.domain.adapter

import android.content.Context
import android.view.View
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import coil.load
import n7.ad2.data.source.local.Repository

fun List<String>.toVOGuideBestVersus(context: Context): List<ImageView> = this.map { heroName ->
    ImageView(context).apply {
        layoutParams = ConstraintLayout.LayoutParams(128, 72)
        id = View.generateViewId()
        load(Repository.getFullUrlHeroImage(heroName))
    }
}
