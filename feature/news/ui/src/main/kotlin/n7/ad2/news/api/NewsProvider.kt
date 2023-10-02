package n7.ad2.news.api

import androidx.fragment.app.Fragment
import n7.ad2.navigator.api.NewsApi
import n7.ad2.news.internal.screen.list.NewsFragment

class NewsProvider : NewsApi {

    override fun getFragment(): Fragment = NewsFragment.getInstance()

}