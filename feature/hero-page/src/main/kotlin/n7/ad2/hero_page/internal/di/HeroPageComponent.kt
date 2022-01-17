package n7.ad2.hero_page.internal.di

import n7.ad2.hero_page.api.HeroPageDependencies
import n7.ad2.hero_page.internal.guides.HeroGuideFragment
import n7.ad2.hero_page.internal.guides.HeroGuideViewModel
import n7.ad2.hero_page.internal.info.HeroInfoFragment
import n7.ad2.hero_page.internal.info.HeroInfoViewModel
import n7.ad2.hero_page.internal.pager.HeroPageFragment
import n7.ad2.hero_page.internal.responses.ResponsesFragment
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

    fun inject(fragment: HeroPageFragment)
    fun inject(fragment: HeroInfoFragment)
    fun inject(fragment: HeroGuideFragment)
    fun inject(fragment: ResponsesFragment)

    val heroInfoViewModelFactory: HeroInfoViewModel.Factory
    val heroGuideViewModelFactory: HeroGuideViewModel.Factory
    val responsesViewModelFactory: ResponsesViewModel.Factory

}