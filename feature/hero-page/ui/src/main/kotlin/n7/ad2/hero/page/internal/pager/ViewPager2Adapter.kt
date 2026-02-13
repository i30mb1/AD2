package n7.ad2.hero.page.internal.pager

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import n7.ad2.hero.page.internal.info.HeroInfoFragment
import n7.ad2.hero.page.internal.responses.ResponsesFragment

class ViewPager2Adapter(fragment: Fragment, private val heroName: String) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment = when (position) {
        0 -> HeroInfoFragment.newInstance(heroName)
        else -> ResponsesFragment.newInstance(heroName)
//            else -> HeroGuideFragment.newInstance(heroName)
    }
}
