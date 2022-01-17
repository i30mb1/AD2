package n7.ad2.hero_page.internal.guides

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.launch
import n7.ad2.android.findDependencies
import n7.ad2.hero_page.R
import n7.ad2.hero_page.databinding.FragmentHeroGuideBinding
import n7.ad2.hero_page.internal.StickyHeaderDecorator
import n7.ad2.hero_page.internal.di.DaggerHeroPageComponent
import n7.ad2.ktx.viewModel
import javax.inject.Inject

class HeroGuideFragment : Fragment(R.layout.fragment_hero_guide) {

    @Inject lateinit var heroesViewModelFactory: HeroGuideViewModel.Factory

    private lateinit var binding: FragmentHeroGuideBinding
    private val viewModel: HeroGuideViewModel by viewModel { heroesViewModelFactory.create() }

    companion object {
        private const val HERO_NAME = "HERO_NAME"
        fun newInstance(heroName: String): HeroGuideFragment = HeroGuideFragment().apply {
            arguments = bundleOf(HERO_NAME to heroName)
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        DaggerHeroPageComponent.factory().create(findDependencies()).inject(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentHeroGuideBinding.bind(view)
        // region Click
        val heroName = requireArguments().getString(HERO_NAME)!!
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
        lifecycleScope.launch {
            viewModel.error
                .flowWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.State.STARTED)
//                .collect(::showDialogError)
        }
        viewModel.loadHeroWithGuides(heroName).observe(viewLifecycleOwner, heroGuideAdapter::submitList)
    }

}