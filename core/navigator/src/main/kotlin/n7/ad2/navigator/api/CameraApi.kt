package n7.ad2.navigator.api

import androidx.fragment.app.Fragment

interface CameraApi {
    fun getFragment(): Class<out Fragment>
}
