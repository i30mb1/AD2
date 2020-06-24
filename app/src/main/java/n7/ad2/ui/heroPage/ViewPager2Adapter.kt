package n7.ad2.ui.heroPage

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import n7.ad2.heroes.full.GuideFragment
import n7.ad2.ui.heroInfo.HeroInfoFragment
import n7.ad2.ui.heroResponse.ResponsesFragment

class ViewPager2Adapter(
        activity: FragmentActivity
) : FragmentStateAdapter(activity) {

    override fun getItemCount(): Int = 3

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> HeroInfoFragment.newInstance()
            1 -> ResponsesFragment.newInstance()
            else -> GuideFragment.newInstance()
        }
    }
}