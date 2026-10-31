import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.google.common.truth.Truth
import n7.ad2.games.leaderboard.LocalPlayerStore
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
internal class LocalPlayerStoreTest {

    @Test
    fun `LocalPlayerStore keeps the same id`() {
        val context = ApplicationProvider.getApplicationContext<Context>()

        val first = LocalPlayerStore(context).localPlayer()
        val second = LocalPlayerStore(context).localPlayer()

        Truth.assertThat(first.id).isNotEmpty()
        Truth.assertThat(second.id).isEqualTo(first.id)
        Truth.assertThat(first.name).isNotEmpty()
    }
}
