package n7.ad2.navigator.api

import androidx.fragment.app.Fragment

interface TournamentsApi {

    fun getFragment(): Class<out Fragment>

    /** How many matches are being played right now, loads the match list from the network. */
    suspend fun getLiveMatchesCount(): Int
}
