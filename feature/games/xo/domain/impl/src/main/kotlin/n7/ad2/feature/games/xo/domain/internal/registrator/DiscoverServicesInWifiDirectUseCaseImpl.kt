package n7.ad2.feature.games.xo.domain.internal.registrator

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.NetworkInfo
import android.net.wifi.p2p.WifiP2pDevice
import android.net.wifi.p2p.WifiP2pDeviceList
import android.net.wifi.p2p.WifiP2pInfo
import android.net.wifi.p2p.WifiP2pManager
import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceInfo
import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceRequest
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.RECEIVER_EXPORTED
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import n7.ad2.app.logger.Logger
import n7.ad2.feature.games.xo.domain.DiscoverServicesInWifiDirectUseCase
import n7.ad2.feature.games.xo.domain.model.Server
import n7.ad2.feature.games.xo.domain.model.WifiDirectServer

internal class DiscoverServicesInWifiDirectUseCaseImpl(
    private val context: Context,
    private val wifiP2pManager: WifiP2pManager,
    private val wifiP2pManagerChannel: WifiP2pManager.Channel,
    private val logger: Logger,
) : DiscoverServicesInWifiDirectUseCase {

    data class WifiDirectState(val isEnabled: Boolean = false, val devices: List<WifiP2pDevice> = emptyList())

    val _state = MutableStateFlow<WifiDirectState>(WifiDirectState())

    @SuppressLint("MissingPermission")
    // слушатель который соберает информацию о доступных серверах в сети
    override fun discover() {
        // для получения метаданных о серверах
        val txtListener = WifiP2pManager.DnsSdTxtRecordListener { fullDomain, record, device ->
            logger.log("DnsSdTxtRecordListener: ${device.deviceAddress}")
        }
        // для получения базовой информации
        val servListener = WifiP2pManager.DnsSdServiceResponseListener { instanceName, registrationType, resourceType ->
            logger.log("DnsSdServiceResponseListener: $registrationType")
        }
        wifiP2pManager.setDnsSdResponseListeners(wifiP2pManagerChannel, servListener, txtListener)

        // регистрируем запрос на поиск служб
        wifiP2pManager.addServiceRequest(
            wifiP2pManagerChannel,
            // поиск с конкретными параметрами
            WifiP2pDnsSdServiceRequest.newInstance("_test", CommonSettings().serviceType),
            // сообщает о результатах добавления запроса
            object : WifiP2pManager.ActionListener {
                override fun onSuccess() {
                    logger.log("listeners onSuccess")
                }

                override fun onFailure(code: Int) {
                    logger.log("listeners onFailure")
                }
            },
        )

        // инициируем процесс поиска служб, указанных в addServiceRequest
        wifiP2pManager.discoverServices(
            wifiP2pManagerChannel,
            object : WifiP2pManager.ActionListener {
                override fun onSuccess() {
                    logger.log("run searching onSuccess")
                }

                override fun onFailure(code: Int) {
                    // Command failed. Check for P2P_UNSUPPORTED, ERROR, or BUSY
                    when (code) {
                        WifiP2pManager.P2P_UNSUPPORTED -> Unit
                    }
                }
            },
        )
    }

    @SuppressLint("MissingPermission")
    // зарегистрировать свой сервис в сети
    override fun startRegistration() {
        //  доп информация о сервере
        val record: Map<String, String> = mapOf(
            "listenport" to "888",
            "buddyname" to "John Doe",
        )

        // указываем имя сервера, его тип и передаем доп информацию
        val serviceInfo = WifiP2pDnsSdServiceInfo.newInstance("_test", CommonSettings().serviceType, record)

        val listener = object : WifiP2pManager.ActionListener {
            override fun onSuccess() {
                logger.log("Register success")
            }

            override fun onFailure(arg0: Int) {
                logger.log("Register failure")
            }
        }
        // регестрируем
        wifiP2pManager.addLocalService(wifiP2pManagerChannel, serviceInfo, listener)

        // создаем группу чтобы к нам могли присоединится
        wifiP2pManager.createGroup(
            wifiP2pManagerChannel,
            object : WifiP2pManager.ActionListener {
                override fun onSuccess() {
                    println("")
                }

                override fun onFailure(reason: Int) {
                    println("")
                }
            },
        )
    }

    @SuppressLint("MissingPermission")
    // поиск устройств с WIFI-Direct
    override operator fun invoke(): Flow<List<Server>> = callbackFlow<List<Server>> {
        startRegistration()
        val peers = mutableListOf<WifiP2pDevice>()
        val intentFilter = IntentFilter().apply {
            addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION) // сообщает включенном/выключенном Wi-Fi Direct
            addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION) // сообщает о подключении/отключении устройств
            addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION) //
            addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION)
        }
        val peerListListener = WifiP2pManager.PeerListListener { peerList: WifiP2pDeviceList ->
            logger.log("WifiDirect receive devices: ${peerList.deviceList.size}")
            val refreshedPeers = peerList.deviceList
            if (refreshedPeers != peers) {
                peers.clear()
                peers.addAll(refreshedPeers)
                trySend(
                    refreshedPeers.map { device ->
                        WifiDirectServer(device.deviceName, device.deviceAddress, 0)
                    },
                )
            }
        }
        val receiver = object : BroadcastReceiver() {
            @SuppressLint("MissingPermission")
            override fun onReceive(context: Context, intent: Intent) {
                logger.log("WifiDirect onReceive: ${intent.action}")
                when (intent.action) {
                    WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION -> {
                        val state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1)
                        val isWifiDirect = state == WifiP2pManager.WIFI_P2P_STATE_ENABLED
                        _state.update { it.copy(isEnabled = isWifiDirect) }
                    }

                    // список изменился, нужно опросить и получить доступные девайсы
                    WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION -> {
                        wifiP2pManager.requestPeers(wifiP2pManagerChannel, peerListListener)
                    }

                    WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION -> {
                        val networkInfo = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                            intent.getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO, NetworkInfo::class.java)
                        } else {
                            @Suppress("DEPRECATION")
                            intent.getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO)
                        } as NetworkInfo
                        if (networkInfo.isConnected) {
                            // подключились к устройству, узнаем к какому, этим методом можно узнать когда мы уже подключились к устройству
                            wifiP2pManager.requestConnectionInfo(
                                wifiP2pManagerChannel,
                                object : WifiP2pManager.ConnectionInfoListener {
                                    override fun onConnectionInfoAvailable(info: WifiP2pInfo?) {
                                        println("")
                                    }
                                },
                            )
                        } else {
                            // отключились от устройства
                        }
                    }
                    // информация об этом девайсе
                    WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION -> {
                        val devices = intent.getParcelableExtra(WifiP2pManager.EXTRA_WIFI_P2P_DEVICE, WifiP2pDevice::class.java) as WifiP2pDevice
                        _state.update { it.copy(devices = it.devices + devices) }
                    }

                    else -> Unit
                }
            }
        }
        ContextCompat.registerReceiver(context, receiver, intentFilter, RECEIVER_EXPORTED)
        delay(2_000)
        wifiP2pManager.discoverPeers(
            wifiP2pManagerChannel,
            object : WifiP2pManager.ActionListener {
                override fun onSuccess() {
                    println("asdf")
                }

                override fun onFailure(reason: Int) {
                    println("asdf")
                }
            },
        )
        awaitClose {
            context.unregisterReceiver(receiver)
        }
    }.onStart { emit(emptyList()) }
}
