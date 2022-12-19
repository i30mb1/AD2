package n7.ad2.news.api

import androidx.fragment.app.Fragment
import n7.ad2.news.internal.screen.list.NewsFragment
import n7.ad2.provider.api.NewsApi

class NewsProvider : NewsApi {

    override fun getFragment(): Fragment = NewsFragment.getInstance()

}