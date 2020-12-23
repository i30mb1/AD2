package n7.ad2.ui.heroGuide

import android.animation.LayoutTransition
import android.content.Context
import android.transition.TransitionManager
import android.util.AttributeSet
import android.view.View
import androidx.constraintlayout.helper.widget.Flow
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.children
import n7.ad2.utils.extension.toPx

class HeroFlow(
    context: Context,
    attributeSet: AttributeSet,
) : ConstraintLayout(context, attributeSet) {

    private val flow: Flow = Flow(context, attributeSet).apply {
        setPadding(2.toPx)
        layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
        setHorizontalGap(2.toPx)
        setVerticalGap(2.toPx)
        setHorizontalStyle(Flow.CHAIN_PACKED)
        setVerticalStyle(Flow.CHAIN_PACKED)
        setHorizontalAlign(Flow.HORIZONTAL_ALIGN_START)
        setVerticalAlign(Flow.VERTICAL_ALIGN_BASELINE)
        setWrapMode(Flow.WRAP_CHAIN)
    }

    init {
        addView(flow)
    }

    fun setHeroes(guideHeroesLst: List<View>) {
        children.filter { it !is Flow }
            .forEach(::removeView)
        TransitionManager.beginDelayedTransition(this)
        guideHeroesLst.forEach {
            addView(it)
            flow.addView(it)
        }
        TransitionManager.endTransitions(this)
    }

}