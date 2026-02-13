package n7.ad2.itempage.api

import androidx.fragment.app.Fragment
import n7.ad2.itempage.internal.ItemInfoFragment
import n7.ad2.navigator.api.ItemPageApi

class ItemPageProvider : ItemPageApi {
    override fun getItemPageFragment(itemName: String): Fragment = ItemInfoFragment.getInstance(itemName)
}
