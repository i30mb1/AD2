package n7.ad2.xo.internal

import android.net.nsd.NsdManager
import android.net.nsd.NsdServiceInfo
import android.util.Log
import javax.inject.Inject

class RegisterServer @Inject constructor(
    private val discoverSettings: DiscoverSettings,
) {

    fun register(manager: NsdManager) {
        val listener = object : NsdManager.RegistrationListener {
            override fun onServiceRegistered(info: NsdServiceInfo) {
                val name = info.serviceName // Android may have change name in order to resolve a conflict
            }

            override fun onRegistrationFailed(serviceInfo: NsdServiceInfo, errorCode: Int) {
                Log.d("N7", errorCode.toString())
            }

            override fun onServiceUnregistered(arg0: NsdServiceInfo) {
                Log.d("N7", "Service unregister")
            }

            override fun onUnregistrationFailed(serviceInfo: NsdServiceInfo, errorCode: Int) {
                Log.d("N7", "Service register")
            }
        }
        val service = NsdServiceInfo()
        service.serviceName = "Game"
        service.serviceType = discoverSettings.serviceType
        service.port = 8086
        manager.registerService(service, NsdManager.PROTOCOL_DNS_SD, listener)
    }
}
