package n7.ad2.ui.heroGuide

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.constraintlayout.helper.widget.Flow
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.children
import n7.ad2.utils.extension.toDp

class HeroFlow(
    context: Context,
    attributeSet: AttributeSet,
) : ConstraintLayout(context, attributeSet) {

    private val flow: Flow = Flow(context, attributeSet).apply {
        setPadding(2.toDp)
        layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
        setHorizontalGap(2.toDp)
        setVerticalGap(2.toDp)
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
        guideHeroesLst.forEach {
            addView(it)
            flow.addView(it)
        }
    }

}