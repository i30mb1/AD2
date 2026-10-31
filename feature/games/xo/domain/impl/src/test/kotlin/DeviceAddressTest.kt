import com.google.common.truth.Truth
import n7.ad2.feature.games.xo.domain.internal.registrator.pickDeviceAddress
import org.junit.Test
import java.net.InetAddress

internal class DeviceAddressTest {

    @Test
    fun `WHEN link-local IPv6 comes before IPv4 THEN IPv4 is picked`() {
        val addresses = listOf(InetAddress.getByName("fe80::fc02:f7ff:fe09:b2c2"), InetAddress.getByName("192.168.31.189"))

        Truth.assertThat(pickDeviceAddress(addresses)?.hostAddress).isEqualTo("192.168.31.189")
    }

    @Test
    fun `WHEN only global IPv6 exists THEN it is picked over link-local`() {
        val addresses = listOf(InetAddress.getByName("fe80::1"), InetAddress.getByName("2001:db8::5"))

        Truth.assertThat(pickDeviceAddress(addresses)?.hostAddress).isEqualTo("2001:db8:0:0:0:0:0:5")
    }

    @Test
    fun `WHEN only link-local exists THEN it is still returned`() {
        val addresses = listOf(InetAddress.getByName("fe80::1"))

        Truth.assertThat(pickDeviceAddress(addresses)).isEqualTo(addresses.single())
    }

    @Test
    fun `WHEN no addresses THEN null`() {
        Truth.assertThat(pickDeviceAddress(emptyList())).isNull()
    }
}
