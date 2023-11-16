package n7.ad2.feature.games.xo.domain.internal.registrator

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.wifi.p2p.WifiP2pDeviceList
import android.net.wifi.p2p.WifiP2pManager
import android.os.Looper
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.RECEIVER_EXPORTED
import androidx.core.content.getSystemService
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.onStart
import n7.ad2.feature.games.xo.domain.DiscoverServicesInWifiDirectUseCase
import n7.ad2.feature.games.xo.domain.model.Server

class DiscoverServicesInWifiDirectUseCaseImpl(
    private val context: Context,
) : DiscoverServicesInWifiDirectUseCase {

    @SuppressLint("MissingPermission")
    override operator fun invoke() = callbackFlow<List<Server>> {
        val manager: WifiP2pManager = context.getSystemService()!!
        val managerChannel = manager.initialize(context, Looper.getMainLooper(), null)
        val intentFilter = IntentFilter().apply {
            addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION)
            addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION)
            addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION)
            addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION)
        }
        val peers = mutableListOf<Server>()
        val peerListListener = WifiP2pManager.PeerListListener { peerList: WifiP2pDeviceList ->
            val refreshedPeers = peerList.deviceList
            if (refreshedPeers != peers) {
                peers.clear()
                peers.addAll(refreshedPeers.map { device ->
                    Server(
                        device.deviceName,
                        device.deviceAddress,
                        0,
                        true
                    )
                })
                trySend(peers)
            }
        }
        val receiver = object : BroadcastReceiver() {
            @SuppressLint("MissingPermission")
            override fun onReceive(context: Context, intent: Intent) {
                when (intent.action) {
                    WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION -> {
                        val state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1)
                        val isWifiP2pEnabled = state == WifiP2pManager.WIFI_P2P_STATE_ENABLED
                    }

                    else -> Unit
                }
                manager.requestPeers(managerChannel, peerListListener)
            }
        }
        ContextCompat.registerReceiver(context, receiver, intentFilter, RECEIVER_EXPORTED)
        val actionListener = object : WifiP2pManager.ActionListener {
            override fun onSuccess() = Unit
            override fun onFailure(reasonCode: Int) = Unit
        }
        manager.discoverPeers(managerChannel, actionListener)
        awaitClose {
            context.unregisterReceiver(receiver)
            manager.stopPeerDiscovery(managerChannel, actionListener)
        }
    }
        .onStart { emit(emptyList()) }
}
