package n7.ad2.feature.games.xo.domain.internal.registrator

import android.annotation.SuppressLint
import android.net.wifi.WpsInfo
import android.net.wifi.p2p.WifiP2pConfig
import android.net.wifi.p2p.WifiP2pManager
import n7.ad2.app.logger.Logger
import n7.ad2.feature.games.xo.domain.ConnectToWifiDirectUseCase

class ConnectToWifiDirectUseCaseImpl(
    private val wifiP2pManager: WifiP2pManager,
    private val wifiP2pManagerChannel: WifiP2pManager.Channel,
    private val logger: Logger,
): ConnectToWifiDirectUseCase {
    @SuppressLint("MissingPermission")
    override suspend fun invoke(serverIP: String) {
        val config = WifiP2pConfig()
        config.deviceAddress = serverIP
        config.wps.setup = WpsInfo.PBC
        wifiP2pManager.connect(wifiP2pManagerChannel, config, object : WifiP2pManager.ActionListener {

            override fun onSuccess() {
                wifiP2pManager.requestGroupInfo(wifiP2pManagerChannel) { group ->
                    val groupPassword = group.passphrase
                }
                logger.log("WifiDirect connect onSuccess")
            }

            override fun onFailure(reason: Int) {
                logger.log("WifiDirect connect onFailure")
            }
        })
    }
}
