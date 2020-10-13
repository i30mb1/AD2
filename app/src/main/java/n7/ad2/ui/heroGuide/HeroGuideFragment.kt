package n7.ad2.ui.heroGuide

import android.os.Bundle
import android.view.View
import androidx.constraintlayout.helper.widget.Flow
import androidx.core.os.bundleOf
import androidx.core.view.children
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.observe
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import androidx.work.workDataOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.yield
import n7.ad2.R
import n7.ad2.databinding.FragmentHeroGuideBinding
import n7.ad2.di.injector
import n7.ad2.ui.heroPage.showDialogError
import n7.ad2.utils.viewModelWithSavedStateHandle

class HeroGuideFragment : Fragment(R.layout.fragment_hero_guide) {

    private lateinit var binding: FragmentHeroGuideBinding
    private val viewModel: HeroGuideViewModel by viewModelWithSavedStateHandle { injector.heroGuideViewModelFactory }

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
        viewModel.loadHeroWithGuides(heroName, requireContext())
        loadNewHeroGuide(heroName)
        viewModel.guide.observe(viewLifecycleOwner) { vo ->
            lifecycleScope.launch {
                binding.root.children
                    .asIterable()
                    .filter { it !is Flow }
                    .forEach { binding.root.removeView(it) }

                vo.heroBestVersus.forEach {
                    binding.root.addView(it)
                    binding.flowHeroBestVersus.addView(it)
                }

                vo.heroWorstVersus.forEach {
                    binding.root.addView(it)
                    binding.flowHeroWorstVersus.addView(it)
                }
            }
        }
        // endregion
    }

    private fun loadNewHeroGuide(heroName: String) {
        val data = workDataOf(HeroGuideWorker.HERO_NAME to heroName)
        val request = OneTimeWorkRequestBuilder<HeroGuideWorker>()
            .setInputData(data)
            .build()

        WorkManager.getInstance(requireContext()).getWorkInfoByIdLiveData(request.id).observe(viewLifecycleOwner) {
            when (it?.state) {
                WorkInfo.State.FAILED -> {
                    requireActivity().showDialogError(it.outputData.getString(HeroGuideWorker.RESULT)!!)
                }
                else -> {
                }
            }
        }

        WorkManager.getInstance(requireContext()).enqueue(request)
    }


}