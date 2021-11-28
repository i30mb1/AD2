package n7.ad2.streams.api

import androidx.fragment.app.Fragment
import n7.ad2.streams.internal.StreamsFragment

object StreamsProvider {

    fun getStreamsFragment(): Fragment {
        return StreamsFragment.getInstance()
    }

}