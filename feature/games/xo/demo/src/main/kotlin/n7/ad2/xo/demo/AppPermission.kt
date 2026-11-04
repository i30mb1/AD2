package n7.ad2.xo.demo

import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity

class AppPermission(private val activity: FragmentActivity, private val onPermissionGranted: () -> Unit) {

    private val requiredPermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        arrayOf(android.Manifest.permission.NEARBY_WIFI_DEVICES)
    } else {
        arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION)
    }
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
