package n7.ad2.ui.itemInfo

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import n7.ad2.R
import n7.ad2.databinding.FragmentItemsBinding

class ItemInfoFragment : Fragment(R.layout.fragment_item_info) {

    private lateinit var binding: FragmentItemsBinding

    companion object {
        fun newInstance(): ItemInfoFragment = ItemInfoFragment()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentItemsBinding.bind(view).also {
            it.lifecycleOwner = viewLifecycleOwner
        }
    }

}