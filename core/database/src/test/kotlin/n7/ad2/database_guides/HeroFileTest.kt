package n7.ad2.database_guides

import androidx.test.filters.LargeTest
import com.squareup.moshi.Moshi
import org.junit.Ignore
import org.junit.Test

@LargeTest
class HeroFileTest {

    private val moshi = Moshi.Builder().build()

    @Ignore("github action error")
    @Test
    fun `file with heroes exists and contains more than 100 heroes`() {
//        val fileWithHeroes = File("${System.getProperty("user.dir")}\\src\\main\\assets\\heroes.json")
//        Truth.assertThat(fileWithHeroes.exists()).isTrue()
//
//        val text = fileWithHeroes.readText()
//
//        val typeAssetsHero = Types.newParameterizedType(List::class.java, n7.ad2.heroes.domain.internal.data.model.AssetsHero::class.java)
//        val adapter: JsonAdapter<List<n7.ad2.heroes.domain.internal.data.model.AssetsHero>> = moshi.adapter(typeAssetsHero)
//        val listAssetsHero = adapter.fromJson(text)
//
//        Truth.assertThat(listAssetsHero?.size).isGreaterThan(100)
    }

}
