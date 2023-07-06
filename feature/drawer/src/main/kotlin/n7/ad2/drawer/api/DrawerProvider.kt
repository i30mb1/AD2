package n7.ad2.drawer.api

import androidx.fragment.app.Fragment
import n7.ad2.drawer.internal.DrawerFragment
import n7.ad2.navigator.api.DrawerApi

class DrawerProvider : DrawerApi {
    override fun getDrawerFragment(): Fragment = DrawerFragment.getInstance()
}