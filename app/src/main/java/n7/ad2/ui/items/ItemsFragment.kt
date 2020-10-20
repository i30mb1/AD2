package n7.ad2.ui.items

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import n7.ad2.R
import n7.ad2.databinding.FragmentItemsBinding
import n7.ad2.databinding.ItemItemBinding
import n7.ad2.di.injector
import n7.ad2.ui.MainActivity
import n7.ad2.ui.itemInfo.ItemInfoActivity
import n7.ad2.ui.itemInfo.ItemInfoFragment
import n7.ad2.ui.items.domain.vo.VOItem
import n7.ad2.utils.viewModel

class ItemsFragment : Fragment(R.layout.fragment_items) {

    private val viewModel: ItemsViewModel by viewModel { injector.itemsViewModel }
    private lateinit var binding: FragmentItemsBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentItemsBinding.bind(view).also {
            it.lifecycleOwner = viewLifecycleOwner
        }

        requireActivity().apply {
            setTitle(R.string.items)
            sendBroadcast(Intent(MainActivity.LOG_ON_RECEIVE).putExtra(MainActivity.LOG_ON_RECEIVE, "item_fragment_created"))
        }
        setHasOptionsMenu(true)
        setupAdapter()
    }

    fun startItemInfoFragment(model: VOItem, binding: ItemItemBinding) {
        val intent = Intent(requireContext(), ItemInfoActivity::class.java)
        intent.putExtra(ItemInfoFragment.ITEM_NAME, model.name)
        startActivity(intent)
    }

    private fun setupAdapter() {
        val myAdapter = ItemsPagedListAdapter(this)
        binding.rv.apply {
            setHasFixedSize(true)
            layoutManager = GridLayoutManager(context, 4)
            adapter = myAdapter
        }
        viewModel.itemsPagedList.observe(viewLifecycleOwner, myAdapter::submitList)
    }

}