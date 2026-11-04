package n7.ad2.games.internal

import android.Manifest
import android.os.Build

// Wi-Fi Direct: с Android 13 отдельное разрешение без геолокации, раньше - точная геолокация,
// первым идёт то, без которого не работает; точную без приблизительной система молча игнорирует
internal fun wifiDirectPermissions(sdkInt: Int): Array<String> = if (sdkInt >= Build.VERSION_CODES.TIRAMISU) {
    arrayOf(Manifest.permission.NEARBY_WIFI_DEVICES)
} else {
    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
}
