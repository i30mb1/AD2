package n7.ad2.ui.streams

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import n7.ad2.R
import n7.ad2.databinding.FragmentStreamsBinding
import n7.ad2.di.injector
import n7.ad2.ui.streams.domain.vo.VOSimpleStream
import n7.ad2.utils.ImageLoader
import n7.ad2.utils.viewModel
import javax.inject.Inject

class StreamsFragment : Fragment(R.layout.fragment_streams) {

    companion object {
        fun getInstance() = StreamFragment()
    }

    @Inject lateinit var imageLoader: ImageLoader

    private var _binding: FragmentStreamsBinding? = null
    private val binding: FragmentStreamsBinding get() = _binding!!
    private val viewModel: StreamsViewModel by viewModel { injector.streamsViewModel }
    private val onStreamClick: (vOSimpleStream: VOSimpleStream) -> Unit = { voSimpleStream ->
        childFragmentManager.commit {
            addToBackStack(null)
            replace(R.id.container, StreamFragment.newInstance(voSimpleStream.streamerName))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        injector.inject(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentStreamsBinding.bind(view)
        setupAdapter()
        viewModel.error.observe(viewLifecycleOwner) { throwable ->
            if (throwable == null) return@observe
            Toast.makeText(requireContext(), throwable.toString(), Toast.LENGTH_LONG).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupAdapter() {
        val streamsPagedListAdapter = StreamsPagedListAdapter(imageLoader, onStreamClick)
        binding.rv.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = streamsPagedListAdapter
            addItemDecoration(DividerItemDecorator())
            setHasFixedSize(true)
        }

        streamsPagedListAdapter.addLoadStateListener { loadState ->
            when (loadState.refresh) {
                is LoadState.NotLoading -> Unit
                LoadState.Loading -> Unit
                is LoadState.Error -> Toast.makeText(requireContext(), (loadState.refresh as LoadState.Error).error.toString(), Toast.LENGTH_LONG).show()
            }
        }
        lifecycleScope.launch {
            viewModel.streams
                .flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
                .collect(streamsPagedListAdapter::submitData)
        }

    }
}