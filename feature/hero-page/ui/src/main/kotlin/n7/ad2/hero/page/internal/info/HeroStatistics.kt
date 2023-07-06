package n7.ad2.hero.page.internal.info

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import n7.ad2.feature.heropage.ui.databinding.HeroStatisticsBinding


// link about @JVMOverload https://proandroiddev.com/misconception-about-kotlin-jvmoverloads-for-android-view-creation-cb88f432e1fe
@SuppressLint("SetTextI18n")
class HeroStatistics(
    context: Context,
    attributeSet: AttributeSet,
) : ConstraintLayout(context, attributeSet) {

    data class Statistics(val strength: Double, val agility: Double, val intelligence: Double)

    private val binding: HeroStatisticsBinding = HeroStatisticsBinding.inflate(LayoutInflater.from(context), this)

    fun setHeroStatistics(statistics: Statistics) {
        binding.tvAgility.text = "${statistics.agility}"
        binding.tvStrength.text = "${statistics.strength}"
        binding.tvIntelligence.text = "${statistics.intelligence}"
    }

}