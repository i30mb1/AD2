package n7.ad2.xo.demo

import android.content.pm.PackageManager
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity

class AppPermission(private val activity: FragmentActivity, private val onPermissionGranted: () -> Unit) {

    private val requiredPermission = buildList {
        add(android.Manifest.permission.ACCESS_FINE_LOCATION)
        add(android.Manifest.permission.NEARBY_WIFI_DEVICES)
    }.toTypedArray()
    private val permission = activity.registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
        onPermissionGranted()
    }

    fun run() {
        if (allPermissionGranted()) {
            onPermissionGranted()
        } else {
            permission.launch(requiredPermission)
        }
    }

    private fun allPermissionGranted() = requiredPermission.all { permission ->
        ContextCompat.checkSelfPermission(activity, permission) == PackageManager.PERMISSION_GRANTED
    }
}
