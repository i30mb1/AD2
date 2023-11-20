package n7.ad2.feature.games.xo.domain

interface ConnectToWifiDirectUseCase {
    suspend operator fun invoke(serverIP: String)
}
