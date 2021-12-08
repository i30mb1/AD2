package n7.ad2.hero_page.internal.di

import n7.ad2.hero_page.api.HeroPageDependencies
import n7.ad2.hero_page.internal.guides.HeroGuideViewModel
import n7.ad2.hero_page.internal.info.HeroInfoViewModel
import n7.ad2.hero_page.internal.pager.HeroPageFragment
import n7.ad2.hero_page.internal.responses.ResponsesViewModel

@dagger.Component(
    dependencies = [
        HeroPageDependencies::class,
    ],
)
interface HeroPageComponent {

    @dagger.Component.Factory
    interface Factory {
        fun create(dependencies: HeroPageDependencies): HeroPageComponent
    }

    fun inject(drawerFragment: HeroPageFragment)

    val heroInfoViewModelFactory: HeroInfoViewModel.Factory
    val heroGuideViewModelFactory: HeroGuideViewModel.Factory
    val responsesViewModelFactory: ResponsesViewModel.Factory

}