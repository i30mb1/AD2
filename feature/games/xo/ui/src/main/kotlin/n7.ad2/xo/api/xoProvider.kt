package n7.ad2.xo.api

import androidx.fragment.app.Fragment
import n7.ad2.xo.internal.XoFragment
import n7.ad2.navigator.api.XoApi

class XoProvider : XoApi {
    override fun getFragment(): Class<out Fragment> = XoFragment::class.java
}
