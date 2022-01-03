package n7.ad2.android

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction

val Fragment.getNavigator: Navigator
    get() = (activity as Navigator)

interface Navigator {
    fun setMainFragment(fragment: Fragment, body: FragmentTransaction.() -> Unit = {})
}