package n7.ad2.items.api

import androidx.fragment.app.Fragment
import n7.ad2.items.internal.ItemsFragment
import n7.ad2.provider.api.ItemsApi

class ItemsProvider : ItemsApi {

    override fun getFragment(): Fragment = ItemsFragment.getInstance()

}