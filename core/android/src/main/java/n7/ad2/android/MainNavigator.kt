package n7.ad2.android

import androidx.fragment.app.Fragment

interface MainNavigator {
    fun setMainFragment(fragment: Fragment, addToBackStack: Boolean = true)
}