package n7.ad2.ui.heroGuide.domain.adapter

import android.app.Application
import android.view.View
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import coil.api.load
import n7.ad2.data.source.local.Repository
import n7.ad2.ui.heroGuide.domain.model.LocalGuideJson
import n7.ad2.ui.heroGuide.domain.vo.VOHeroGuide

fun LocalGuideJson.toVO(application: Application): VOHeroGuide {
    return VOHeroGuide().also {
    it.heroBestVersus = heroBestVersus.map {
            ImageView(application).apply {
                layoutParams = ConstraintLayout.LayoutParams(128, 72)
                id = View.generateViewId()
                load("file:///android_asset/heroes/${it}/${Repository.ASSETS_FILE_FULL}")
            }
        }
    }
}