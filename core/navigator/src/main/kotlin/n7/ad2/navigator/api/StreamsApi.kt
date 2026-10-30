package n7.ad2.navigator.api

import android.content.Context
import android.content.Intent
import androidx.fragment.app.Fragment

interface StreamsApi {

    fun getFragment(): Class<out Fragment>

    /** Opens the in-app player for a Twitch channel. */
    fun getStreamIntent(context: Context, streamerName: String): Intent
}
