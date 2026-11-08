package n7.ad2.hero.page.internal.responses

import java.io.File
import java.io.FileNotFoundException
import java.io.InputStream
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import n7.ad2.AppLocale
import n7.ad2.Resources
import n7.ad2.coroutines.DispatchersProvider
import n7.ad2.hero.page.internal.responses.domain.interactor.GetHeroResponsesInteractor
import n7.ad2.hero.page.internal.responses.domain.model.LocalHeroResponsesItem
import n7.ad2.hero.page.internal.responses.domain.model.Response
import n7.ad2.hero.page.internal.responses.domain.usecase.ConvertLocalHeroToVOResponseUseCase
import n7.ad2.hero.page.internal.responses.domain.vo.VOResponse
import n7.ad2.repositories.ResponseRepository
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

// ResponseRepository reads android.os.Environment
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ResponsesTest {

    private val dispatchers = object : DispatchersProvider() {
        override val IO: CoroutineDispatcher = Dispatchers.Unconfined
    }

    // the wiki has no voice lines of Kez and Largo in English, of Largo in Russian either
    private val assetsWithoutResponses = object : Resources {
        override fun getAssets(path: String): InputStream = throw FileNotFoundException(path)
        override fun getExternalFilesDir(path: String): File? = null
        override fun getString(resourceID: Int): String = ""
        override fun getString(resourceID: Int, formatArgs: Any): String = ""
        override fun getFilesDir(): File? = null
        override fun getConfiguration(): Any? = null
    }

    @Test
    fun `a hero without voice lines in a language has none instead of an error`() = runBlocking {
        val interactor = GetHeroResponsesInteractor(ConvertLocalHeroToVOResponseUseCase(dispatchers), ResponseRepository(assetsWithoutResponses), dispatchers)

        assertEquals(emptyList<VOResponse>(), interactor("Kez", AppLocale.English).first())
    }

    @Test
    fun `the arcana voice of a line is marked, the hero the line is about shows its icon`() = runBlocking {
        val rivals = LocalHeroResponsesItem(
            category = "Rivals",
            responses = listOf(
                Response("https://wiki/vo_juggernaut_jugg_rival_01.mp3/revision/latest?cb=1", "It's not the size of the sword, Sven.", listOf("heroes/sven/minimap.webp")),
                Response("https://wiki/vo_juggernaut_jugsc_arc_rival_01.mp3/revision/latest?cb=2", "It's not the size of the sword, Sven.", listOf("heroes/sven/minimap.webp"), isArcane = true),
            ),
        )

        val lines = ConvertLocalHeroToVOResponseUseCase(dispatchers)("Juggernaut", listOf(rivals), emptyList()).filterIsInstance<VOResponse.Body>()

        assertEquals(listOf(false, true), lines.map { it.isArcane })
        lines.forEach { line -> assertEquals(listOf("file:///android_asset/heroes/sven/minimap.webp"), line.icons.map { it.imageUrl }) }
    }
}
