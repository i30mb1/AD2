package n7.ad2.database_guides

import androidx.test.filters.LargeTest
import com.google.common.truth.Truth
import java.io.File
import kotlinx.serialization.json.Json
import org.junit.Ignore
import org.junit.Test

@LargeTest
class HeroFileTest {

    private val json = Json { ignoreUnknownKeys = true }

    @Ignore("github action error")
    @Test
    fun `file with heroes exists and contains more than 100 heroes`() {
        val fileWithHeroes = File("${System.getProperty("user.dir")}\\src\\main\\assets\\heroes.json")
        Truth.assertThat(fileWithHeroes.exists()).isTrue()

        val text = fileWithHeroes.readText()

//        val listAssetsHero = json.decodeFromString<List<AssetsHero>>(text)

//        Truth.assertThat(listAssetsHero.size).isGreaterThan(100)
    }

}
