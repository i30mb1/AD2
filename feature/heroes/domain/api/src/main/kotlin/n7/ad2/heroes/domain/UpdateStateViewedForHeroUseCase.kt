package n7.ad2.heroes.domain

interface UpdateStateViewedForHeroUseCase {

    suspend operator fun invoke(name: String)
}
