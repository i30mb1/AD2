package n7.ad2.ui.streams

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import n7.ad2.R
import n7.ad2.databinding.FragmentStreamsBinding
import n7.ad2.di.injector
import n7.ad2.utils.viewModel

class StreamsFragment : Fragment(R.layout.fragment_streams) {

    private lateinit var binding: FragmentStreamsBinding
    private val viewModel: StreamsViewModel by viewModel { injector.streamsViewModel }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentStreamsBinding.bind(view)
        setupAdapter()
    }

    private fun setupAdapter() {
        val streamsPagedListAdapter = StreamsPagedListAdapter()
        binding.rv.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = streamsPagedListAdapter
        }

        lifecycleScope.launch {
            viewModel.streams
                .flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
                .collect(streamsPagedListAdapter::submitData)
        }

    }
}