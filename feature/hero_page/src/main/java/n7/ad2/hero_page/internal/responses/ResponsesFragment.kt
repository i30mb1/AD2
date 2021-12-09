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
import n7.ad2.hero_page.internal.pager.HeroPageFragment
import n7.ad2.hero_page.internal.pager.showDialogError
import n7.ad2.hero_page.internal.responses.domain.vo.VOResponseBody
import n7.ad2.ktx.viewModel
import javax.inject.Inject

class ResponsesFragment : Fragment(R.layout.fragment_hero_responses) {

    companion object {
        private const val HERO_NAME = "HERO_NAME"
        fun newInstance(heroName: String): ResponsesFragment = ResponsesFragment().apply {
            arguments = bundleOf(HERO_NAME to heroName)
        }
    }

    @Inject lateinit var responsesViewModelFactory: ResponsesViewModel.Factory

    private lateinit var responsesPagedListAdapter: ResponsesAdapter
    private lateinit var binding: FragmentHeroResponsesBinding
    private lateinit var downloadResponseManager: DownloadResponseManager
    private val viewModel: ResponsesViewModel by viewModel { responsesViewModelFactory.create("") }
    private val infoPopupWindow: n7.ad2.hero_page.internal.info.InfoPopupWindow by lazy(LazyThreadSafetyMode.NONE) { n7.ad2.hero_page.internal.info.InfoPopupWindow(requireContext(), lifecycle) }

    @InternalCoroutinesApi
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentHeroResponsesBinding.bind(view)

        downloadResponseManager = DownloadResponseManager(requireActivity().contentResolver, requireActivity().application, lifecycle)
        downloadResponseManager.setDownloadListener {
            when (it) {
                is DownloadSuccess -> viewModel.refreshResponses()
                is DownloadFailed -> showDialogError(it.error)
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
        responsesPagedListAdapter = ResponsesAdapter((requireActivity() as HeroPageFragment).audioExoPlayer, infoPopupWindow) {
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