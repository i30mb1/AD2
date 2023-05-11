package n7.ad2.heropage.internal.pager

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.OrientationEventListener
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.transition.Slide
import androidx.transition.TransitionManager
import com.robinhood.ticker.TickerUtils
import com.robinhood.ticker.TickerView
import n7.ad2.AppLocale
import n7.ad2.android.extension.load
import n7.ad2.feature.heropage.R
import n7.ad2.ktx.dpToPx

class AnimatedToolbar(context: Context, attr: AttributeSet) : Toolbar(context, attr) {

    private var onChangeResponseLocaleListener: ((appLocale: AppLocale) -> Unit)? = null
    private val params = LayoutParams(30.dpToPx, 30.dpToPx).apply {
        gravity = Gravity.CENTER
    }
    private val ivHero: ImageView = ImageView(context).apply {
        layoutParams = params
        contentDescription = context.getString(R.string.desc_hero_mini_avatar)
        addView(this)
    }
    private val tvLocale: TickerView = TickerView(context, null, n7.ad2.core.ui.R.style.TextAppearance_B1).apply {
        layoutParams = params.apply { width = 60.dpToPx }
        setCharacterLists(TickerUtils.provideAlphabeticalList())
        animationDuration = resources.getInteger(n7.ad2.core.ui.R.integer.animation_long).toLong()
        visibility = GONE
        gravity = Gravity.CENTER
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
        duration = resources.getInteger(n7.ad2.core.ui.R.integer.animation_medium).toLong()
        interpolator = AccelerateDecelerateInterpolator()
    }
    private val listener = object : OrientationEventListener(context) {
        override fun onOrientationChanged(orientation: Int) {
            ivHero.rotation = 360 - orientation.toFloat()
        }
    }
    private var currentLocale: AppLocale? = null

    init {
        listener.enable()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        listener.disable()
        onChangeResponseLocaleListener = null
    }

    fun setOnChangeHeroLocaleListener(listener: (appLocale: AppLocale) -> Unit) {
        this.onChangeResponseLocaleListener = listener
    }

    fun loadHero(heroName: String, appLocale: AppLocale) {
        ivHero.load(n7.ad2.repositories.HeroRepository.getFullUrlHeroMinimap(heroName))
        currentLocale = appLocale
        setUpViews()
    }

    private fun setUpViews() {
        tvLocale.text = currentLocale?.value
        tvLocale.setOnClickListener { view ->
            val locale = if (currentLocale is AppLocale.Russian) AppLocale.English else AppLocale.Russian
            (view as TextView).text = locale.value
            onChangeResponseLocaleListener?.invoke(locale)
        }
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
//            1 -> tvLocale.visibility = visibility
//            2 -> ivRefresh.visibility= visibility
        }
    }

}