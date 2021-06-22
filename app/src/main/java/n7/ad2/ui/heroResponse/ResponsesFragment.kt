package n7.ad2.ui.heroResponse

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.collect
import n7.ad2.R
import n7.ad2.databinding.FragmentHeroResponsesBinding
import n7.ad2.di.injector
import n7.ad2.ui.heroInfo.InfoPopupWindow
import n7.ad2.ui.heroPage.HeroPageActivity
import n7.ad2.ui.heroPage.showDialogError
import n7.ad2.ui.heroResponse.domain.vo.VOResponseBody
import n7.ad2.utils.StickyHeaderDecorator
import n7.ad2.utils.viewModel

class ResponsesFragment : Fragment(R.layout.fragment_hero_responses) {

    private lateinit var responsesPagedListAdapter: ResponsesAdapter
    private lateinit var binding: FragmentHeroResponsesBinding
    private lateinit var downloadResponseManager: DownloadResponseManager
    private val viewModel: ResponsesViewModel by viewModel { injector.responsesViewModel }
    private val infoPopupWindow: InfoPopupWindow by lazy(LazyThreadSafetyMode.NONE) { InfoPopupWindow(requireContext(), lifecycle) }

    companion object {
        private const val HERO_NAME = "HERO_NAME"
        fun newInstance(heroName: String): ResponsesFragment = ResponsesFragment().apply {
            arguments = bundleOf(HERO_NAME to heroName)
        }
    }

    @InternalCoroutinesApi
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentHeroResponsesBinding.bind(view).also {
            it.lifecycleOwner = viewLifecycleOwner
        }

        downloadResponseManager = DownloadResponseManager(requireActivity().contentResolver, requireActivity().application, lifecycle)
        downloadResponseManager.setDownloadListener {
            when (it) {
                is DownloadSuccess -> viewModel.refreshResponses()
                is DownloadFailed -> showDialogError(it.error)
            }
        }

        lifecycleScope.launchWhenResumed { viewModel.error.collect(::showDialogError) }
        val heroName = requireArguments().getString(HERO_NAME)!!
        (requireActivity() as HeroPageActivity).binding.toolbar.setOnChangeHeroLocaleListener {
            viewModel.loadResponses(heroName, it)
        }
        viewModel.loadResponses(heroName)
        setupPagedListAdapter()
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
        responsesPagedListAdapter = ResponsesAdapter((requireActivity() as HeroPageActivity).audioExoPlayer, infoPopupWindow) {
            createDialogResponse(it)
        }
        binding.rv.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            addItemDecoration(StickyHeaderDecorator(responsesPagedListAdapter, this))
            adapter = responsesPagedListAdapter
        }

        viewModel.voResponses.observe(viewLifecycleOwner, responsesPagedListAdapter::submitList)
    }
}