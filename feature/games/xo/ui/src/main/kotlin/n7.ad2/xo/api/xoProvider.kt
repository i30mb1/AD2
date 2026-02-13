package n7.ad2.xo.api

import androidx.fragment.app.Fragment
import n7.ad2.navigator.api.XoApi
import n7.ad2.xo.internal.XoFragment

class XoProvider : XoApi {
    override fun getFragment(): Class<out Fragment> = XoFragment::class.java
}
