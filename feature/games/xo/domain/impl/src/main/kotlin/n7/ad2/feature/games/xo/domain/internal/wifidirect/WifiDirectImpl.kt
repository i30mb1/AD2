package n7.ad2.feature.games.xo.domain.internal.wifidirect

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.wifi.WpsInfo
import android.net.wifi.p2p.WifiP2pConfig
import android.net.wifi.p2p.WifiP2pManager
import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceInfo
import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceRequest
import android.os.Looper
import androidx.core.content.ContextCompat
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withTimeoutOrNull
import n7.ad2.app.logger.Logger
import n7.ad2.feature.games.xo.domain.WifiDirect
import n7.ad2.feature.games.xo.domain.model.WifiDirectRoom
import java.net.InetAddress
import kotlin.coroutines.resume

/**
 * Хост - автономный владелец группы: группа есть до прихода гостя, ее адрес гость получает при входе.
 * Объявление комнаты - DNS-SD сервис в Wi-Fi Direct с портом в TXT записи.
 * Без разрешения система кидает SecurityException: ловим и считаем, что Wi-Fi Direct недоступен.
 */
@SuppressLint("MissingPermission")
internal class WifiDirectImpl(private val context: Context, private val logger: Logger) : WifiDirect {

    private companion object {
        const val INSTANCE = "ad2"
        const val SERVICE_TYPE = "_ad2room._tcp"
        const val CONNECT_TIMEOUT_MS = 30_000L

        // поиск в Wi-Fi Direct сам гаснет через ~2 минуты, а хост виден, только пока сам ищет
        const val REDISCOVER_MS = 15_000L
    }

    private val manager: WifiP2pManager? by lazy { context.getSystemService(WifiP2pManager::class.java) }
    private val channel: WifiP2pManager.Channel? by lazy { manager?.initialize(context, Looper.getMainLooper(), null) }

    override suspend fun advertise(name: String, port: Int) {
        val manager = manager ?: return
        val channel = channel ?: return
        action { manager.clearLocalServices(channel, it) }
        // старая группа от прошлой комнаты мешает createGroup (BUSY)
        action { manager.removeGroup(channel, it) }
        if (!action { manager.createGroup(channel, it) }) return
        val info = WifiP2pDnsSdServiceInfo.newInstance(INSTANCE, SERVICE_TYPE, WifiDirectRecord.encode(name, port))
        if (action { manager.addLocalService(channel, info, it) }) action { manager.discoverPeers(channel, it) }
    }

    override fun discover(): Flow<List<WifiDirectRoom>> {
        val manager = manager ?: return emptyFlow()
        val channel = channel ?: return emptyFlow()
        return callbackFlow {
            val found = LinkedHashMap<String, WifiDirectRoom>()
            val txtListener = WifiP2pManager.DnsSdTxtRecordListener { fullDomain, record, device ->
                if (!fullDomain.contains(SERVICE_TYPE)) return@DnsSdTxtRecordListener
                val room = WifiDirectRecord.decode(record, device.deviceAddress) ?: return@DnsSdTxtRecordListener
                found[room.deviceAddress] = room
                trySend(found.values.toList())
            }
            manager.setDnsSdResponseListeners(channel, { _, _, _ -> }, txtListener)
            val request = WifiP2pDnsSdServiceRequest.newInstance(SERVICE_TYPE)
            action { manager.addServiceRequest(channel, request, it) }
            launch {
                while (isActive) {
                    action { manager.discoverServices(channel, it) }
                    delay(REDISCOVER_MS)
                }
            }
            awaitClose { runCatching { manager.removeServiceRequest(channel, request, null) } }
        }
    }

    override suspend fun connect(deviceAddress: String): InetAddress? {
        val manager = manager ?: return null
        val channel = channel ?: return null
        val config = WifiP2pConfig().apply {
            this.deviceAddress = deviceAddress
            wps.setup = WpsInfo.PBC
            groupOwnerIntent = 0 // владелец группы - хост
        }
        return withTimeoutOrNull(CONNECT_TIMEOUT_MS) {
            callbackFlow {
                val receiver = object : BroadcastReceiver() {
                    override fun onReceive(context: Context, intent: Intent) {
                        manager.requestConnectionInfo(channel) { info ->
                            if (info != null && info.groupFormed && !info.isGroupOwner) info.groupOwnerAddress?.let { trySend(it) }
                        }
                    }
                }
                ContextCompat.registerReceiver(context, receiver, IntentFilter(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION), ContextCompat.RECEIVER_NOT_EXPORTED)
                if (!action { manager.connect(channel, config, it) }) close()
                awaitClose { context.unregisterReceiver(receiver) }
            }.first()
        }.also { if (it == null) logger.log("WifiDirect connect to $deviceAddress failed") }
    }

    override fun release() {
        val manager = manager ?: return
        val channel = channel ?: return
        runCatching {
            manager.clearLocalServices(channel, null)
            manager.cancelConnect(channel, null)
            manager.removeGroup(channel, null)
        }.onFailure { logger.log("WifiDirect release: $it") }
    }

    private suspend fun action(call: (WifiP2pManager.ActionListener) -> Unit): Boolean = suspendCancellableCoroutine { continuation ->
        val listener = object : WifiP2pManager.ActionListener {
            override fun onSuccess() {
                if (continuation.isActive) continuation.resume(true)
            }

            override fun onFailure(reason: Int) {
                logger.log("WifiDirect action failed: $reason")
                if (continuation.isActive) continuation.resume(false)
            }
        }
        runCatching { call(listener) }.onFailure { error ->
            logger.log("WifiDirect action error: $error")
            if (continuation.isActive) continuation.resume(false)
        }
    }
}
