package n7.ad2.ui.items

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.observe
import androidx.recyclerview.widget.GridLayoutManager
import n7.ad2.R
import n7.ad2.databinding.FragmentItemsBinding
import n7.ad2.di.injector
import n7.ad2.ui.MainActivity
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