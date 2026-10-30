package n7.ad2.streams.api

import android.content.Context
import android.content.Intent
import androidx.fragment.app.Fragment
import n7.ad2.navigator.api.StreamsApi
import n7.ad2.streams.internal.StreamsFragment
import n7.ad2.streams.internal.stream.StreamActivity

class StreamsProvider : StreamsApi {

    override fun getFragment(): Class<out Fragment> = StreamsFragment::class.java

    override fun getStreamIntent(context: Context, streamerName: String): Intent = StreamActivity.newInstance(context, streamerName)
}
