package n7.ad2.feature.games.xo.domain.internal.registrator

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.NetworkInfo
import android.net.wifi.p2p.WifiP2pDevice
import android.net.wifi.p2p.WifiP2pDeviceList
import android.net.wifi.p2p.WifiP2pManager
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.RECEIVER_EXPORTED
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.onStart
import n7.ad2.app.logger.Logger
import n7.ad2.feature.games.xo.domain.DiscoverServicesInWifiDirectUseCase
import n7.ad2.feature.games.xo.domain.model.Server

internal class DiscoverServicesInWifiDirectUseCaseImpl(
    private val context: Context,
    private val wifiP2pManager: WifiP2pManager,
    private val wifiP2pManagerChannel: WifiP2pManager.Channel,
    private val logger: Logger,
) : DiscoverServicesInWifiDirectUseCase {

    @SuppressLint("MissingPermission")
    override operator fun invoke(): Flow<List<Server>> = callbackFlow<List<Server>> {
        val intentFilter = IntentFilter().apply {
            addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION)
            addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION)
            addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION)
            addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION)
        }
        val peers = mutableListOf<WifiP2pDevice>()
        val peerListListener = WifiP2pManager.PeerListListener { peerList: WifiP2pDeviceList ->
            logger.log("WifiDrect receive devices: ${peerList.deviceList.size}")
            val refreshedPeers = peerList.deviceList
            if (refreshedPeers != peers) {
                peers.clear()
                peers.addAll(refreshedPeers)
//                trySend(refreshedPeers.map { device ->
//                    SimpleServer(
//                        device.deviceName,
//                        device.deviceAddress,
//                        0,
//                    )
//                })
            }
        }
        val receiver = object : BroadcastReceiver() {
            @SuppressLint("MissingPermission")
            override fun onReceive(context: Context, intent: Intent) {
                logger.log("WifiDirect onReceive: ${intent.action}")
                when (intent.action) {
                    WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION -> {
                        val state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1)
                        val isWifiP2pEnabled = state == WifiP2pManager.WIFI_P2P_STATE_ENABLED
                    }

                    WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION -> {
                        val networkInfo: NetworkInfo = intent
                            .getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO, NetworkInfo::class.java) as NetworkInfo
                        logger.log("net")
                    }

                    else -> Unit
                }
                wifiP2pManager.requestPeers(wifiP2pManagerChannel, peerListListener)
            }
        }
        ContextCompat.registerReceiver(context, receiver, intentFilter, RECEIVER_EXPORTED)
        val actionListener = object : WifiP2pManager.ActionListener {
            override fun onSuccess() {
                logger.log("WifiDirect onSuccess")
            }

            override fun onFailure(reasonCode: Int) {
                logger.log("WifiDirect onFailure")
            }
        }
        wifiP2pManager.discoverPeers(wifiP2pManagerChannel, actionListener)
        awaitClose {
            context.unregisterReceiver(receiver)
            wifiP2pManager.stopPeerDiscovery(wifiP2pManagerChannel, actionListener)
        }
    }
        .onStart { emit(emptyList()) }
}
