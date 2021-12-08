package n7.ad2.provider.api

import androidx.fragment.app.Fragment

interface ItemPageApi {
    fun getItemPageFragment(itemName: String): Fragment
}
