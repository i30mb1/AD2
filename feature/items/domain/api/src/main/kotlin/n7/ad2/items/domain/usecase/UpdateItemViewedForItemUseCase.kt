package n7.ad2.items.domain.usecase

interface UpdateItemViewedForItemUseCase {
    suspend operator fun invoke(name: String)
}
