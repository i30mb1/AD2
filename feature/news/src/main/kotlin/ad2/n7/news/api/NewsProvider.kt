package ad2.n7.news.api

import ad2.n7.news.internal.list.NewsFragment
import androidx.fragment.app.Fragment
import n7.ad2.provider.api.NewsApi

class NewsProvider : NewsApi {

    override fun getFragment(): Fragment = NewsFragment.getInstance()

}