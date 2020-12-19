package n7.ad2.ui.heroGuide

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import androidx.work.workDataOf
import n7.ad2.R
import n7.ad2.databinding.FragmentHeroGuideBinding
import n7.ad2.di.injector
import n7.ad2.ui.heroPage.showDialogError
import n7.ad2.utils.StickyHeaderDecorator
import n7.ad2.utils.viewModel

class HeroGuideFragment : Fragment(R.layout.fragment_hero_guide) {

    private lateinit var binding: FragmentHeroGuideBinding
    private val viewModel: HeroGuideViewModel by viewModel { injector.heroGuideViewModel }

    companion object {
        private const val HERO_NAME = "HERO_NAME"
        fun newInstance(heroName: String): HeroGuideFragment = HeroGuideFragment().apply {
            arguments = bundleOf(HERO_NAME to heroName)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentHeroGuideBinding.bind(view).also {
            it.lifecycleOwner = viewLifecycleOwner
            it.vieModel = viewModel
        }
        // region Click
        val heroName = requireArguments().getString(HERO_NAME)!!
        loadNewHeroGuide(heroName)
        // endregion
        setupGuideRecyclerView(heroName)
    }

    private fun setupGuideRecyclerView(heroName: String) {
        val heroGuideAdapter = HeroGuideAdapter()
        val linearLayoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.rv.apply {
            adapter = heroGuideAdapter
            layoutManager = linearLayoutManager
            addItemDecoration(StickyHeaderDecorator(heroGuideAdapter, this))
        }
        viewModel.loadHeroWithGuides(heroName).observe(viewLifecycleOwner, heroGuideAdapter::submitList)
    }

    private fun loadNewHeroGuide(heroName: String) {
        val data = workDataOf(HeroGuideWorker.HERO_NAME to heroName)
        val request = OneTimeWorkRequestBuilder<HeroGuideWorker>()
            .setInputData(data)
            .build()

        WorkManager.getInstance(requireContext()).getWorkInfoByIdLiveData(request.id).observe(viewLifecycleOwner) {
            when (it?.state) {
                WorkInfo.State.FAILED -> requireActivity().showDialogError(it.outputData.getString(HeroGuideWorker.RESULT)!!)
                else -> {
                }
            }
        }

        WorkManager.getInstance(requireContext()).enqueue(request)
    }


}