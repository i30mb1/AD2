package n7.ad2.ui.heroResponse

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.observe
import androidx.recyclerview.widget.LinearLayoutManager
import n7.ad2.R
import n7.ad2.databinding.FragmentHeroResponsesBinding
import n7.ad2.di.injector
import n7.ad2.ui.heroPage.HeroPageActivity
import n7.ad2.ui.heroPage.HeroPageViewModel
import n7.ad2.ui.heroPage.showDialogError
import n7.ad2.ui.heroResponse.domain.vo.VOResponseBody
import n7.ad2.utils.StickyHeaderDecorator
import n7.ad2.utils.viewModel

class ResponsesFragment : Fragment(R.layout.fragment_hero_responses) {

    private lateinit var responsesPagedListAdapter: ResponsesListAdapter
    private lateinit var binding: FragmentHeroResponsesBinding
    private lateinit var downloadResponseManager: DownloadResponseManager
    private val viewModel: ResponsesViewModel by viewModel { injector.responsesViewModel }
    private val heroPageViewModel: HeroPageViewModel by activityViewModels()

    companion object {
        fun newInstance(): ResponsesFragment = ResponsesFragment()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentHeroResponsesBinding.bind(view).also {
            it.lifecycleOwner = viewLifecycleOwner
        }

        downloadResponseManager = DownloadResponseManager(requireActivity().contentResolver, Handler(Looper.getMainLooper()), requireActivity().application, lifecycle)
        downloadResponseManager.setDownloadListener {
            when (it) {
                is DownloadSuccess -> heroPageViewModel.refresh()
                is DownloadFailed -> requireActivity().showDialogError(it.error)
            }

        }
        viewModel.error.observe(viewLifecycleOwner) {
            it?.let {
                requireActivity().showDialogError(it)
            }
        }
        heroPageViewModel.hero.observe(viewLifecycleOwner, viewModel::loadResponses)
        heroPageViewModel.locale.observe(viewLifecycleOwner, viewModel::loadResponsesLocale)
        setupPagedListAdapter()
    }

    private fun createDialogResponse(item: VOResponseBody) {
        childFragmentManager.setFragmentResultListener(DialogResponse.REQUEST_KEY, this) { _: String, bundle: Bundle ->
            when (bundle.getString(DialogResponse.RESULT_KEY)) {
                DialogResponse.ACTION_DOWNLOAD_RESPONSE -> downloadResponseManager.download(item)
            }
        }
        val dialogResponse = DialogResponse.newInstance()
        dialogResponse.show(childFragmentManager, null)
    }

    private fun setupPagedListAdapter() {
        responsesPagedListAdapter = ResponsesListAdapter((requireActivity() as HeroPageActivity).audioExoPlayer) {
            createDialogResponse(it)
        }
        binding.rv.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            addItemDecoration(StickyHeaderDecorator(this, responsesPagedListAdapter))
            adapter = responsesPagedListAdapter
        }

        viewModel.voResponses.observe(viewLifecycleOwner) {
            responsesPagedListAdapter.submitList(it)
        }
    }
}