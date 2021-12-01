package n7.ad2.streams.internal

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import n7.ad2.android.DrawerPercentListener
import n7.ad2.android.extension.viewModel
import n7.ad2.android.findDependencies
import n7.ad2.logger.AD2Logger
import n7.ad2.streams.R
import n7.ad2.streams.databinding.FragmentStreamsBinding
import n7.ad2.streams.internal.adapter.StreamsItemDecorator
import n7.ad2.streams.internal.adapter.StreamsPagedListAdapter
import n7.ad2.streams.internal.di.DaggerStreamsComponent
import n7.ad2.streams.internal.domain.vo.VOStream
import javax.inject.Inject

internal class StreamsFragment : Fragment(R.layout.fragment_streams) {

    companion object {
        fun getInstance() = StreamsFragment()
    }

    @Inject lateinit var streamsFactory: StreamsViewModel.Factory
    @Inject lateinit var logger: AD2Logger

    private var _binding: FragmentStreamsBinding? = null
    private val binding: FragmentStreamsBinding get() = _binding!!
    private val viewModel: StreamsViewModel by viewModel { streamsFactory.create() }
    private val streamsItemDecorator = StreamsItemDecorator()
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
        setupInsets()
        logger.log("streams fragment created")
        viewModel.error.observe(viewLifecycleOwner) { throwable ->
            if (throwable == null) return@observe
            Toast.makeText(requireContext(), throwable.toString(), Toast.LENGTH_LONG).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.rv) { _, insets ->
            val statusBarsInsets = insets.getInsets(WindowInsetsCompat.Type.statusBars())
            val navigationBarsInsets = insets.getInsets(WindowInsetsCompat.Type.navigationBars())
            streamsItemDecorator.statusBarsInsets = statusBarsInsets.top
            streamsItemDecorator.navigationBarsInsets = navigationBarsInsets.bottom
            insets
        }
        (parentFragment as DrawerPercentListener).setDrawerPercentListener { percent ->
            streamsItemDecorator.percent = percent
            binding.rv.invalidateItemDecorations()
        }
    }

    private fun setupAdapter() {
        val streamsPagedListAdapter = StreamsPagedListAdapter(layoutInflater, onStreamClick)
        binding.rv.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = streamsPagedListAdapter
            addItemDecoration(streamsItemDecorator)
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