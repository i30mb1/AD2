package n7.ad2.feature.games.xo.domain

interface GetDeviceNameUseCase {
    suspend operator fun invoke(): String
}
