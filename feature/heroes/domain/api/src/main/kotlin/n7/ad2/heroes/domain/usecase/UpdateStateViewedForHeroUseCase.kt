package n7.ad2.heroes.domain.usecase

interface UpdateStateViewedForHeroUseCase {

    suspend operator fun invoke(name: String)
}
