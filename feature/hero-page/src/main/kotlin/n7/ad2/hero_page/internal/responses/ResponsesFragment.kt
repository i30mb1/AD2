package n7.ad2.hero_page.internal.responses

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.InternalCoroutinesApi
import n7.ad2.hero_page.R
import n7.ad2.hero_page.databinding.FragmentHeroResponsesBinding
import n7.ad2.hero_page.internal.StickyHeaderDecorator
import n7.ad2.hero_page.internal.info.InfoPopupWindow
import n7.ad2.hero_page.internal.pager.showDialogError
import n7.ad2.hero_page.internal.responses.domain.vo.VOResponseBody
import n7.ad2.ktx.lazyUnsafe
import n7.ad2.ktx.viewModel
import n7.ad2.media_player.AudioExoPlayer
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
    private lateinit var responsesPagedListAdapter: ResponsesAdapter
    private val downloadResponseManager by lazyUnsafe { DownloadResponseManager(requireActivity().contentResolver, requireActivity().application, lifecycle) }
    private val viewModel: ResponsesViewModel by viewModel { responsesViewModelFactory.create("") }
    private val infoPopupWindow: InfoPopupWindow by lazyUnsafe { InfoPopupWindow(requireContext(), lifecycle) }

    @InternalCoroutinesApi
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentHeroResponsesBinding.bind(view)

        downloadResponseManager.setDownloadListener { result ->
            when (result) {
                is DownloadResult.Success -> viewModel.refreshResponses()
                is DownloadResult.Failed -> showDialogError(result.error)
            }
        }

//        lifecycleScope.launchWhenResumed { viewModel.error.collect(::showDialogError) }
//        val heroName = requireArguments().getString(HERO_NAME)!!
//        (requireActivity() as HeroPageFragment).binding.toolbar.setOnChangeHeroLocaleListener {
//            viewModel.loadResponses(heroName, it)
//        }
//        viewModel.loadResponses(heroName)
//        setupPagedListAdapter()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.rv.adapter = null
        _binding = null
    }

    private fun createDialogResponse(item: VOResponseBody) {
        if (item.isSavedInMemory) return
        childFragmentManager.setFragmentResultListener(DialogResponse.REQUEST_KEY, this) { _: String, bundle: Bundle ->
            when (bundle.getString(DialogResponse.RESULT_KEY)) {
                DialogResponse.ACTION_DOWNLOAD_RESPONSE -> downloadResponseManager.download(item)
                else -> throw UnsupportedOperationException("cannot handle unknown result key")
            }
        }
        val dialogResponse = DialogResponse.newInstance()
        dialogResponse.show(childFragmentManager, null)
    }

    private fun setupPagedListAdapter() {
        responsesPagedListAdapter = ResponsesAdapter() {
            createDialogResponse(it)
        }
        responsesPagedListAdapter.stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
        binding.rv.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            addItemDecoration(StickyHeaderDecorator(responsesPagedListAdapter, this))
            adapter = responsesPagedListAdapter
        }

        viewModel.voResponses.observe(viewLifecycleOwner, responsesPagedListAdapter::submitList)
    }
}