package n7.ad2.android

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction

val Fragment.getMainFragmentNavigator: MainFragmentNavigator
    get() = (activity as MainFragmentNavigator)

interface MainFragmentNavigator {
    fun setMainFragment(fragment: Fragment, body: FragmentTransaction.() -> Unit = {})
}