package n7.ad2.heroes.domain

interface GetHeroByNameUseCase {

    suspend operator fun invoke(name: String): Hero
}
