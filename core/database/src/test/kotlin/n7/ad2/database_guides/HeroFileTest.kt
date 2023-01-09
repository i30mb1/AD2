package n7.ad2.database_guides

import androidx.test.filters.LargeTest
import com.google.common.truth.Truth
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import n7.ad2.database_guides.internal.domain.model.AssetsHero
import org.junit.Test
import java.io.File

@LargeTest
class HeroFileTest {

    private val moshi = Moshi.Builder().build()

    @Test
    fun `file with heroes exists and contains more than 100 heroes`() {
        val fileWithHeroes = File("${System.getProperty("user.dir")}\\src\\main\\assets\\heroes.json")
        Truth.assertThat(fileWithHeroes.exists()).isTrue()

        val text = fileWithHeroes.readText()

        val typeAssetsHero = Types.newParameterizedType(List::class.java, AssetsHero::class.java)
        val adapter: JsonAdapter<List<AssetsHero>> = moshi.adapter(typeAssetsHero)
        val listAssetsHero = adapter.fromJson(text)

        Truth.assertThat(listAssetsHero?.size).isGreaterThan(100)
    }

}