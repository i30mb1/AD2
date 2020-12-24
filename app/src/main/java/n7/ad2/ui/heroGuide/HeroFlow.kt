package n7.ad2.ui.heroGuide

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import androidx.annotation.StyleRes
import androidx.constraintlayout.helper.widget.Flow
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.children
import androidx.transition.TransitionManager
import coil.load
import com.google.android.material.textview.MaterialTextView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import n7.ad2.R
import n7.ad2.utils.extension.toPx

data class VOHeroFlowItem(val heroName: String, val urlHeroImage: String)

class HeroFlow(
    context: Context,
    attributeSet: AttributeSet,
) : ConstraintLayout(context, attributeSet), CoroutineScope by MainScope() {

    private val inflater = LayoutInflater.from(context)
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
        visibility = INVISIBLE
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        cancel()
    }

    fun setHeroesHardToWin(list: List<VOHeroFlowItem>) = setHeroes(list, R.style.TextAppearance_HeroDisadvantage)

    fun setHeroesEasyToWin(list: List<VOHeroFlowItem>) = setHeroes(list, R.style.TextAppearance_HeroAdvantage)

    private fun setHeroes(list: List<VOHeroFlowItem>, @StyleRes style: Int) {
        launch {
            list.asFlow()
                .onStart { clearFlowFromViews() }
                .map { inflateItemHeroFlow(it, style) }
                .flowOn(Dispatchers.IO)
                .onCompletion {
                    TransitionManager.beginDelayedTransition(this@HeroFlow)
                    visibility = VISIBLE
                }
                .collect {
                    addView(it)
                    flow.addView(it)
                }
        }
    }

    private fun clearFlowFromViews() = children.filter { it !is Flow }.map(::removeView)

    private fun inflateItemHeroFlow(item: VOHeroFlowItem, @StyleRes style: Int): View {
        val view = inflater.inflate(R.layout.item_hero_flow, this, false)
        view.findViewById<ImageView>(R.id.iv).load(item.urlHeroImage) {
            error(R.drawable.hero_placeholder)
        }
        view.findViewById<MaterialTextView>(R.id.tv_winrate).apply {
            setTextAppearance(style)
        }
        view.id = generateViewId()
        return view
    }

}