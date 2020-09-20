package n7.ad2.ui.heroPage

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.transition.Slide
import androidx.transition.TransitionManager
import coil.load
import n7.ad2.R
import n7.ad2.data.source.local.Repository
import n7.ad2.data.source.local.ResponseLocale
import n7.ad2.data.source.local.model.LocalHero
import n7.ad2.utils.extension.toPx

class AnimatedToolbar(context: Context, attr: AttributeSet) : Toolbar(context, attr) {

    private var locale = ResponseLocale.valueOf(context.getString(R.string.locale))
    private var onChangeResponseLocaleListener: ((locale: ResponseLocale) -> Unit)? = null
    private val params = LayoutParams(30.toPx.toInt(), 30.toPx.toInt()).apply {
        gravity = Gravity.CENTER
    }
    private val ivHero: ImageView = ImageView(context).apply {
        layoutParams = params
        contentDescription = context.getString(R.string.desc_hero_mini_avatar)
        addView(this)
    }
    private val tvLocale: TextView = TextView(context, null, R.style.TextAppearance_Body1).apply {
        layoutParams = params
        visibility = GONE
        gravity = Gravity.CENTER
        text = locale.value
        setOnClickListener {
            if (text == ResponseLocale.ENG.value) {
                locale = ResponseLocale.RU
                text = ResponseLocale.RU.value
            } else {
                locale = ResponseLocale.ENG
                text = ResponseLocale.ENG.value
            }
        }
        addView(this)
    }
    private var oldPage = -1
    private val transition = Slide().apply {
        duration = resources.getInteger(R.integer.animation_medium).toLong()
        interpolator = AccelerateDecelerateInterpolator()
    }

    fun onChangeResponseLocale(listener: (locale: ResponseLocale) -> Unit) {
        this.onChangeResponseLocaleListener = listener
    }

    fun loadHero(hero: LocalHero) {
        ivHero.load("file:///android_asset/${hero.assetsPath}/${Repository.ASSETS_FILE_MINIMAP}")
    }

    fun pageSelected(newPage: Int) {
        TransitionManager.beginDelayedTransition(this, transition)
        changeVisibilityForPage(newPage, VISIBLE)
        changeVisibilityForPage(oldPage, GONE)
        oldPage = newPage
    }

    private fun changeVisibilityForPage(page: Int, visibility: Int) {
        when (page) {
            0 -> ivHero.visibility = visibility
            1 -> tvLocale.visibility = visibility
        }
    }

}