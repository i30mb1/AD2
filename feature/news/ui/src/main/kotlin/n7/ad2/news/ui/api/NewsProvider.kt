package n7.ad2.news.ui.api

import androidx.fragment.app.Fragment
import n7.ad2.navigator.api.NewsApi
import n7.ad2.news.ui.internal.screen.news.NewsFragment

class NewsProvider : NewsApi {
    override fun getFragment(): Class<out Fragment> = NewsFragment::class.java
}
