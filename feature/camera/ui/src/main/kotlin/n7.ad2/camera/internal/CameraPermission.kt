package n7.ad2.camera.internal

import android.content.pm.PackageManager
import androidx.activity.result.contract.ActivityResultContracts.RequestMultiplePermissions
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity

internal class CameraPermission(
    private val activity: FragmentActivity,
    private val onPermissionGranted: () -> Unit,
) {

    private val requiredPermission = arrayOf(
        android.Manifest.permission.CAMERA,
        android.Manifest.permission.WRITE_EXTERNAL_STORAGE
    )
    private val permission = activity.registerForActivityResult(RequestMultiplePermissions()) {
        onPermissionGranted()
    }

    fun run() {
        if (isGranted()) {
            onPermissionGranted()
        } else {
            requestPermission()
        }
    }

    private fun requestPermission() = permission.launch(requiredPermission)

    private fun isGranted() = requiredPermission.all { permission ->
        ContextCompat.checkSelfPermission(activity, permission) == PackageManager.PERMISSION_GRANTED
    }

}
