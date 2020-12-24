package n7.ad2.ui.heroGuide

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import androidx.constraintlayout.helper.widget.Flow
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.children
import androidx.transition.TransitionManager
import coil.load
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filter
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

    fun setHeroes(list: List<VOHeroFlowItem>) {
        launch {
            val removeViews = children.asFlow()
                .filter { it !is Flow }
                .map(::removeView)

            list.asFlow()
                .onStart { removeViews.collect() }
                .map(::inflateItemHeroFlow)
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


    private fun inflateItemHeroFlow(item: VOHeroFlowItem): View {
        val view = inflater.inflate(R.layout.item_hero_flow, this, false)
        view.findViewById<ImageView>(R.id.iv).load(item.urlHeroImage) {
            error(R.drawable.hero_placeholder)
        }
        view.id = generateViewId()
        return view
    }

}