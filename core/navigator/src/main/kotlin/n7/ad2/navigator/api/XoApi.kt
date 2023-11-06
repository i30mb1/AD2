package n7.ad2.navigator.api

import androidx.fragment.app.Fragment

interface XoApi {
    fun getFragment(): Class<out Fragment>
}
