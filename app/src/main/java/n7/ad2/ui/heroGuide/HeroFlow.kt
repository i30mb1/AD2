package n7.ad2.ui.heroGuide

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
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
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import n7.ad2.R
import n7.ad2.utils.extension.toPx
import kotlinx.coroutines.flow.Flow as CoroutineFlow

data class VOHeroFlowItem(val heroName: String, val urlHeroImage: String, val heroWinrate: String)
data class VOHeroFlowSpell(val skillName: String, val urlImageSkill: String, val skillOrder: String)
data class VOHeroFlowStartingHeroItem(val itemName: String, val urlHeroItem: String)
data class VOHeroFlowHeroItem(val itemName: String, val urlHeroItem: String, val itemTiming: String)

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

    fun setHeroItems(list: List<VOHeroFlowHeroItem>) = setViews(list) { map(::inflateItemHeroItemFlow) }

    fun setStartingHeroItems(list: List<VOHeroFlowStartingHeroItem>) = setViews(list) { map(::inflateItemHeroItemFlow) }

    fun setSkills(list: List<VOHeroFlowSpell>) = setViews(list) { map { inflateItemSpellFlow(it) } }

    private fun setHeroes(list: List<VOHeroFlowItem>, @StyleRes style: Int) = setViews(list) { map { inflateItemHeroFlow(it, style) } }

    private fun <T> setViews(list: List<T>, operation: CoroutineFlow<T>.() -> CoroutineFlow<View>) {
        list.asFlow()
            .onStart { clearFlowFromViews() }
            .operation()
            .flowOn(Dispatchers.IO)
            .onCompletion { TransitionManager.beginDelayedTransition(this@HeroFlow); visibility = VISIBLE }
            .onEach(::addView)
            .onEach { flow.addView(it) }
            .launchIn(this)
    }

    private fun clearFlowFromViews() = children.filter { it !is Flow }.map(::removeView)

    private fun inflateItemHeroItemFlow(item: VOHeroFlowHeroItem): View {
        val view = inflater.inflate(R.layout.flow_hero_item, this, false)
        view.findViewById<ImageView>(R.id.iv_item).load(item.urlHeroItem) { error(R.drawable.item_placeholder) }
        view.findViewById<TextView>(R.id.tv_time).text = item.itemTiming
        view.id = generateViewId()
        return view
    }

    private fun inflateItemHeroItemFlow(item: VOHeroFlowStartingHeroItem): View {
        val view = inflater.inflate(R.layout.flow_hero_item, this, false)
        view.findViewById<ImageView>(R.id.iv_item).load(item.urlHeroItem) { error(R.drawable.item_placeholder) }
        view.findViewById<TextView>(R.id.tv_time).visibility = View.INVISIBLE
        view.id = generateViewId()
        return view
    }

    private fun inflateItemSpellFlow(item: VOHeroFlowSpell): View {
        val view = inflater.inflate(R.layout.flow_spell, this, false)
        view.findViewById<ImageView>(R.id.iv_skill).load(item.urlImageSkill) {
            error(R.drawable.spell_placeholder)
        }
        view.findViewById<TextView>(R.id.tv_lvl).text = item.skillOrder
        view.id = generateViewId()
        return view
    }

    private fun inflateItemHeroFlow(item: VOHeroFlowItem, @StyleRes style: Int): View {
        val view = inflater.inflate(R.layout.flow_hero, this, false)
        view.findViewById<ImageView>(R.id.iv).load(item.urlHeroImage) {
            error(R.drawable.hero_placeholder)
        }
        view.findViewById<MaterialTextView>(R.id.tv_winrate).apply {
            setTextAppearance(style)
            text = item.heroWinrate
        }
        view.id = generateViewId()
        return view
    }

}