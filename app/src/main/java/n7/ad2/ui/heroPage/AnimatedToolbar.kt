package n7.ad2.ui.heroPage

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.widget.ImageView
import androidx.appcompat.widget.Toolbar
import coil.api.load
import n7.ad2.R
import n7.ad2.data.source.local.Repository
import n7.ad2.data.source.local.model.LocalHero
import n7.ad2.utils.extension.toPx

class AnimatedToolbar(context: Context, attr: AttributeSet) : Toolbar(context, attr) {

    private var imageHero: ImageView = ImageView(context).apply {
        val params = LayoutParams(30.toPx.toInt(), 30.toPx.toInt())
        params.gravity = Gravity.CENTER
        layoutParams = params
        contentDescription = context.getString(R.string.desc_hero_mini_avatar)
        addView(this)
    }

    init {
    }

    fun loadHero(hero: LocalHero) {
        imageHero.load("file:///android_asset/${hero.assetsPath}/${Repository.ASSETS_FILE_MINIMAP}")
    }

}