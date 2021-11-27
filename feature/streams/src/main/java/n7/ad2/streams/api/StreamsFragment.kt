package n7.ad2.streams.api

import ad2.n7.android.extension.viewModel
import ad2.n7.android.findDependencies
import android.content.Context
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
import n7.ad2.streams.R
import n7.ad2.streams.databinding.FragmentStreamsBinding
import n7.ad2.streams.internal.StreamsViewModel
import n7.ad2.streams.internal.adapter.StreamsItemDecorator
import n7.ad2.streams.internal.adapter.StreamsPagedListAdapter
import n7.ad2.streams.internal.di.DaggerStreamsComponent
import n7.ad2.streams.internal.domain.vo.VOStream
import javax.inject.Inject

class StreamsFragment : Fragment(R.layout.fragment_streams) {

    companion object {
        fun getInstance() = StreamsFragment()
    }

    @Inject lateinit var streamsFactory: StreamsViewModel.Factory

    private var _binding: FragmentStreamsBinding? = null
    private val binding: FragmentStreamsBinding get() = _binding!!
    private val viewModel: StreamsViewModel by viewModel { streamsFactory.create() }
    private val onStreamClick: (vOSimpleStream: VOStream) -> Unit = { voSimpleStream ->
        childFragmentManager.commit {
//            addToBackStack(null)
//            replace(R.id.container, StreamFragment.newInstance(voSimpleStream.streamerName))
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        DaggerStreamsComponent.factory()
            .create(findDependencies())
            .inject(this)
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
        val streamsPagedListAdapter = StreamsPagedListAdapter(layoutInflater, onStreamClick)
        binding.rv.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = streamsPagedListAdapter
            addItemDecoration(StreamsItemDecorator())
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