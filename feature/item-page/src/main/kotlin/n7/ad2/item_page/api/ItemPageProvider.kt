package n7.ad2.item_page.api

import androidx.fragment.app.Fragment
import n7.ad2.item_page.internal.ItemInfoFragment
import n7.ad2.provider.api.ItemPageApi

class ItemPageProvider : ItemPageApi {
    override fun getItemPageFragment(itemName: String): Fragment = ItemInfoFragment.getInstance(itemName)
}