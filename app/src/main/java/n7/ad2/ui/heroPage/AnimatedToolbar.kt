package n7.ad2.ui.heroPage

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.core.view.isVisible
import androidx.transition.Slide
import androidx.transition.TransitionManager
import androidx.transition.TransitionSet
import coil.api.load
import n7.ad2.R
import n7.ad2.data.source.local.Repository
import n7.ad2.data.source.local.model.LocalHero
import n7.ad2.utils.extension.toPx

class AnimatedToolbar(context: Context, attr: AttributeSet) : Toolbar(context, attr) {

    private val params = LayoutParams(30.toPx.toInt(), 30.toPx.toInt()).apply {
        gravity = Gravity.CENTER
    }

    private val imageHero: ImageView = ImageView(context).apply {
        layoutParams = params
        contentDescription = context.getString(R.string.desc_hero_mini_avatar)
        addView(this)
    }
    private val responseLocale: TextView = TextView(context).apply {
        layoutParams = params
        visibility = GONE
        text = context.getString(R.string.locale)
        addView(this)
    }

    init {
        imageHero.setOnClickListener {
            val slide = Slide()
            slide.slideEdge = Gravity.START
            slide.duration = 300
            slide.addTarget(imageHero)
            val slide2 = Slide()
            slide2.slideEdge = Gravity.END
            slide2.duration = 300
            slide2.addTarget(responseLocale)
            val transition = TransitionSet()
            transition.addTransition(slide)
            transition.addTransition(slide2)
            TransitionManager.beginDelayedTransition(this, transition)
            imageHero.visibility = GONE
            responseLocale.visibility = VISIBLE
        }
    }

    fun loadHero(hero: LocalHero) {
        imageHero.load("file:///android_asset/${hero.assetsPath}/${Repository.ASSETS_FILE_MINIMAP}")
    }

}