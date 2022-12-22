package n7.ad2.heropage.internal.pager

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import n7.ad2.heropage.internal.guides.HeroGuideFragment
import n7.ad2.heropage.internal.info.HeroInfoFragment
import n7.ad2.heropage.internal.responses.ResponsesFragment

class ViewPager2Adapter(
    fragment: Fragment,
    private val heroName: String,
) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int = 3

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> HeroInfoFragment.newInstance(heroName)
            1 -> ResponsesFragment.newInstance(heroName)
            else -> HeroGuideFragment.newInstance(heroName)
        }
    }

}