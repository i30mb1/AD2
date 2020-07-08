package n7.ad2.ui.heroResponse

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.observe
import androidx.recyclerview.widget.LinearLayoutManager
import n7.ad2.R
import n7.ad2.databinding.FragmentHeroResponsesBinding
import n7.ad2.di.injector
import n7.ad2.ui.heroPage.AudioExoPlayer
import n7.ad2.ui.heroPage.DialogError
import n7.ad2.ui.heroPage.HeroPageViewModel
import n7.ad2.utils.StickyHeaderDecorator
import n7.ad2.utils.viewModel

class ResponsesFragment : Fragment(R.layout.fragment_hero_responses) {

    private lateinit var responsesPagedListAdapter: ResponsesListAdapter
    private lateinit var binding: FragmentHeroResponsesBinding
    private val viewModel by viewModel { injector.responsesViewModel }
    private val heroPageViewModel by activityViewModels<HeroPageViewModel>()
    private lateinit var audioExoPlayer: AudioExoPlayer

    companion object {
        fun newInstance(): ResponsesFragment = ResponsesFragment()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentHeroResponsesBinding.bind(view).also {
            it.lifecycleOwner = viewLifecycleOwner
        }
        audioExoPlayer = AudioExoPlayer(requireActivity().application, lifecycle)
        audioExoPlayer.setErrorListener {
            createDialogError(it.message.toString())
        }
        heroPageViewModel.hero.observe(viewLifecycleOwner, viewModel::loadResponses)
        setupPagedListAdapter()
    }


    private fun createDialogError(title: String) {
        val dialogTheme = DialogError.newInstance(title)
        dialogTheme.show(childFragmentManager, null)
    }

    private fun setupPagedListAdapter() {
        responsesPagedListAdapter = ResponsesListAdapter(audioExoPlayer)
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