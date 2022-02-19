package n7.ad2.hero_page.internal.pager

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.OrientationEventListener
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.ImageView
import androidx.appcompat.widget.Toolbar
import androidx.transition.Slide
import androidx.transition.TransitionManager
import com.robinhood.ticker.TickerUtils
import com.robinhood.ticker.TickerView
import n7.ad2.android.Locale
import n7.ad2.android.extension.load
import n7.ad2.hero_page.R
import n7.ad2.ktx.dpToPx

class AnimatedToolbar(context: Context, attr: AttributeSet) : Toolbar(context, attr) {

    private var onChangeResponseLocaleListener: ((locale: Locale) -> Unit)? = null
    private val params = LayoutParams(30.dpToPx, 30.dpToPx).apply {
        gravity = Gravity.CENTER
    }
    private val ivHero: ImageView = ImageView(context).apply {
        layoutParams = params
        contentDescription = context.getString(R.string.desc_hero_mini_avatar)
        addView(this)
    }
    private val tvLocale: TickerView = TickerView(context, null, n7.ad2.ui.R.style.TextAppearance_B1).apply {
        layoutParams = params.apply { width = 60.dpToPx }
        setCharacterList(TickerUtils.getDefaultListForUSCurrency())
        animationDuration = resources.getInteger(n7.ad2.ui.R.integer.animation_long).toLong()
        visibility = GONE
        gravity = Gravity.CENTER
        text = Locale.valueOf(context.getString(n7.ad2.android.R.string.locale)).name
        setOnClickListener {
            val locale = if (text == Locale.ENG.name) Locale.RU else Locale.ENG
            text = locale.name
            onChangeResponseLocaleListener?.invoke(locale)
        }
        addView(this)
    }

    //    private val ivRefresh: ImageView = ImageView(context).apply {
//        layoutParams = params
//        visibility = GONE
//        setImageDrawable(ContextCompat.getDrawable(context, R.drawable.radio_button))
//        setOnClickListener { it.isSelected = !it.isSelected }
//        addView(this)
//    }
    private var oldPage = -1
    private val transition = Slide().apply {
        duration = resources.getInteger(n7.ad2.ui.R.integer.animation_medium).toLong()
        interpolator = AccelerateDecelerateInterpolator()
    }
    private val listener = object : OrientationEventListener(context) {
        override fun onOrientationChanged(orientation: Int) {
            ivHero.rotation = 360 - orientation.toFloat()
        }
    }

    init {
        listener.enable()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        listener.disable()
        onChangeResponseLocaleListener = null
    }

    fun setOnChangeHeroLocaleListener(listener: (locale: Locale) -> Unit) {
        this.onChangeResponseLocaleListener = listener
    }

    fun loadHero(heroName: String) {
        ivHero.load(n7.ad2.repositories.HeroRepository.getFullUrlHeroMinimap(heroName))
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
//            2 -> ivRefresh.visibility= visibility
        }
    }

}