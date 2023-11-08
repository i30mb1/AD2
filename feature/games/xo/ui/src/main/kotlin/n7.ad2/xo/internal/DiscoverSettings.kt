package n7.ad2.xo.internal

import javax.inject.Inject

class DiscoverSettings @Inject constructor() {
    val serviceType = "_websocket._tcp" // "_<protocol>._<transportlayer>"
}
