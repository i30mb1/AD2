package n7.ad2.heropage.internal.di

import n7.ad2.heropage.api.HeroPageDependencies
import n7.ad2.heropage.internal.guides.HeroGuideFragment
import n7.ad2.heropage.internal.guides.HeroGuideViewModel
import n7.ad2.heropage.internal.info.HeroInfoFragment
import n7.ad2.heropage.internal.info.HeroInfoViewModel
import n7.ad2.heropage.internal.pager.HeroPageFragment
import n7.ad2.heropage.internal.responses.ResponsesFragment
import n7.ad2.heropage.internal.responses.ResponsesViewModel

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