import com.google.common.truth.Truth
import n7.ad2.feature.games.xo.domain.internal.wifidirect.WifiDirectRecord
import n7.ad2.feature.games.xo.domain.model.WifiDirectRoom
import org.junit.Test

internal class WifiDirectRecordTest {

    @Test
    fun `WHEN record is encoded and decoded THEN room comes back with the device address and player id`() {
        val record = WifiDirectRecord.encode("Pixel 8", 41235, "id-1")

        Truth.assertThat(WifiDirectRecord.decode(record, "aa:bb:cc:dd:ee:ff")).isEqualTo(WifiDirectRoom("Pixel 8", "aa:bb:cc:dd:ee:ff", 41235, "id-1"))
    }

    @Test
    fun `WHEN record has no player id THEN device address stands for it`() {
        val record = WifiDirectRecord.encode("Pixel 8", 41235, "id-1") - "id"

        Truth.assertThat(WifiDirectRecord.decode(record, "aa:bb")?.id).isEqualTo("aa:bb")
    }

    @Test
    fun `WHEN record belongs to another app THEN it is ignored`() {
        Truth.assertThat(WifiDirectRecord.decode(mapOf("listenport" to "888", "buddyname" to "John Doe"), "aa:bb")).isNull()
    }

    @Test
    fun `WHEN port is missing or broken THEN it is ignored`() {
        val record = WifiDirectRecord.encode("Pixel 8", 41235, "id-1")

        Truth.assertThat(WifiDirectRecord.decode(record - "port", "aa:bb")).isNull()
        Truth.assertThat(WifiDirectRecord.decode(record + ("port" to "x"), "aa:bb")).isNull()
        Truth.assertThat(WifiDirectRecord.decode(record + ("port" to "0"), "aa:bb")).isNull()
    }

    @Test
    fun `WHEN name is blank THEN device address is shown instead`() {
        val record = WifiDirectRecord.encode("", 5000, "id-1")

        Truth.assertThat(WifiDirectRecord.decode(record, "aa:bb")?.name).isEqualTo("aa:bb")
    }

    @Test
    fun `WHEN name is longer than a TXT entry allows THEN it is cut`() {
        val record = WifiDirectRecord.encode("x".repeat(500), 5000, "id-1")

        Truth.assertThat(record.getValue("name").length).isAtMost(WifiDirectRecord.MAX_NAME)
    }
}
