package n7.ad2.hero.page.internal.guides

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import n7.ad2.android.findDependencies
import n7.ad2.core.ui.content
import n7.ad2.hero.page.internal.di.DaggerHeroPageComponent
import n7.ad2.ktx.lazyUnsafe
import n7.ad2.ktx.viewModel
import javax.inject.Inject

class HeroGuideFragment : Fragment() {

    companion object {
        private const val HERO_NAME = "HERO_NAME"
        fun newInstance(heroName: String): HeroGuideFragment = HeroGuideFragment().apply {
            arguments = bundleOf(HERO_NAME to heroName)
        }
    }

    @Inject lateinit var heroesViewModelFactory: HeroGuideViewModel.Factory

    private val heroName by lazyUnsafe { requireArguments().getString(HERO_NAME)!! }
    private val viewModel: HeroGuideViewModel by viewModel { heroesViewModelFactory.create() }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        DaggerHeroPageComponent.factory().create(findDependencies()).inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
        content { HeroGuideScreen(viewModel, heroName) }
}
