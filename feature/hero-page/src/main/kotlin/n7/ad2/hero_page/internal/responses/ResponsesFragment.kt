package n7.ad2.hero_page.internal.responses

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.flow.onEach
import n7.ad2.hero_page.R
import n7.ad2.hero_page.databinding.FragmentHeroResponsesBinding
import n7.ad2.hero_page.internal.info.InfoPopupWindow
import n7.ad2.hero_page.internal.pager.HeroPageFragment
import n7.ad2.hero_page.internal.pager.showDialogError
import n7.ad2.hero_page.internal.responses.adapter.ResponsesAdapter
import n7.ad2.hero_page.internal.responses.domain.vo.VOResponse
import n7.ad2.ktx.lazyUnsafe
import n7.ad2.ktx.viewModel
import n7.ad2.media_player.AudioExoPlayer
import n7.ad2.ui.StickyHeaderDecorator
import javax.inject.Inject

class ResponsesFragment : Fragment(R.layout.fragment_hero_responses) {

    companion object {
        private const val HERO_NAME = "HERO_NAME"
        fun newInstance(heroName: String): ResponsesFragment = ResponsesFragment().apply {
            arguments = bundleOf(HERO_NAME to heroName)
        }
    }

    @Inject lateinit var responsesViewModelFactory: ResponsesViewModel.Factory
    @Inject lateinit var audioExoPlayerFactory: AudioExoPlayer.Factory
    private val audioExoPlayer by lazyUnsafe { audioExoPlayerFactory.create(lifecycle) }

    private var _binding: FragmentHeroResponsesBinding? = null
    private val binding: FragmentHeroResponsesBinding get() = _binding!!
    private val heroName by lazyUnsafe { requireArguments().getString(HERO_NAME)!! }
    private lateinit var responsesPagedListAdapter: ResponsesAdapter
    private val downloadResponseManager by lazyUnsafe { DownloadResponseManager(requireActivity().contentResolver, requireActivity().application, lifecycle) }
    private val viewModel: ResponsesViewModel by viewModel { responsesViewModelFactory.create(heroName) }
    private val infoPopupWindow: InfoPopupWindow by lazyUnsafe { InfoPopupWindow(requireContext(), lifecycle) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentHeroResponsesBinding.bind(view)

        (parentFragment as HeroPageFragment).setOnChangeHeroLocaleListener(viewModel::loadResponses)
        setupPagedListAdapter()
        setupState()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.rv.adapter = null
        _binding = null
    }

    private fun setupState() {
        downloadResponseManager.setDownloadListener { result ->
            when (result) {
                is DownloadResult.Success -> viewModel.refreshResponses()
                is DownloadResult.Failed -> showDialogError(result.error)
            }
        }
        viewModel.state.flowWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.State.STARTED)
            .onEach { state ->
                when (state) {
                    is ResponsesViewModel.State.Data -> {
                        binding.error.isVisible = false
                        responsesPagedListAdapter.submitList(state.list)
                    }
                    is ResponsesViewModel.State.Error -> binding.error.setError(state.error.message)
                    ResponsesViewModel.State.Loading -> Unit
                }
            }
    }

    private fun playSound(item: VOResponse.Body) {
        audioExoPlayer.play(item.audioUrl)
    }

    private fun showPopup() {

    }

    private fun showDialogResponse(item: VOResponse.Body) {
        if (item.isSavedInMemory) return
        childFragmentManager.setFragmentResultListener(DialogResponse.REQUEST_KEY, this) { _: String, bundle: Bundle ->
            when (bundle.getString(DialogResponse.RESULT_KEY)) {
                DialogResponse.ACTION_DOWNLOAD_RESPONSE -> downloadResponseManager.download(item)
                else -> error("cannot handle unknown result key")
            }
        }
        val dialogResponse = DialogResponse.newInstance()
        dialogResponse.show(childFragmentManager, null)
    }

    private fun setupPagedListAdapter() {
        responsesPagedListAdapter = ResponsesAdapter(layoutInflater, ::showDialogResponse, ::playSound, ::showPopup)
        responsesPagedListAdapter.stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
        binding.rv.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            addItemDecoration(StickyHeaderDecorator(responsesPagedListAdapter, this))
            adapter = responsesPagedListAdapter
        }
    }
}