package n7.ad2.streams.api

import androidx.fragment.app.Fragment
import n7.ad2.navigator.api.StreamsApi
import n7.ad2.streams.internal.StreamsFragment

class StreamsProvider : StreamsApi {

    override fun getFragment(): Fragment = StreamsFragment.getInstance()

}
