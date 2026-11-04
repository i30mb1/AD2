import android.Manifest
import android.os.Build
import com.google.common.truth.Truth
import n7.ad2.games.internal.wifiDirectNeedsExplanation
import n7.ad2.games.internal.wifiDirectPermissions
import org.junit.Test

internal class WifiDirectPermissionsTest {

    @Test
    fun `from Android 13 only the nearby devices permission is asked, without location`() {
        Truth.assertThat(wifiDirectPermissions(Build.VERSION_CODES.TIRAMISU).toList())
            .containsExactly(Manifest.permission.NEARBY_WIFI_DEVICES)
    }

    @Test
    fun `below Android 13 fine location goes first and together with coarse, alone the system ignores it`() {
        Truth.assertThat(wifiDirectPermissions(Build.VERSION_CODES.S_V2).toList())
            .containsExactly(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
            .inOrder()
    }

    @Test
    fun `below Android 13 location is explained before the system asks for it`() {
        Truth.assertThat(wifiDirectNeedsExplanation(Build.VERSION_CODES.S_V2, explained = false)).isTrue()
    }

    @Test
    fun `an accepted explanation is not shown again`() {
        Truth.assertThat(wifiDirectNeedsExplanation(Build.VERSION_CODES.S_V2, explained = true)).isFalse()
    }

    @Test
    fun `from Android 13 there is no location to explain`() {
        Truth.assertThat(wifiDirectNeedsExplanation(Build.VERSION_CODES.TIRAMISU, explained = false)).isFalse()
    }
}
