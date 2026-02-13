package n7.ad2.navigator.api

import androidx.fragment.app.Fragment

interface NewsApi {

    fun getFragment(): Class<out Fragment>
}
