package n7.ad2.ui.heroResponse.domain.usecase

import androidx.test.filters.SmallTest
import com.google.common.truth.Truth.assertThat
import com.squareup.moshi.Moshi
import kotlinx.coroutines.ExperimentalCoroutinesApi
import n7.ad2.CoroutineTestRule
import n7.ad2.runBlockingTest
import n7.ad2.ui.heroResponse.domain.model.LocalHeroResponsesItem
import n7.ad2.ui.heroResponse.domain.model.Response
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
@SmallTest
class GetLocalHeroResponsesFromJsonUseCaseTest {

    @get:Rule
    val coroutineTestRule = CoroutineTestRule()

    private val moshi = Moshi.Builder().build()

    val getLocalHeroResponsesFromJsonUseCase = GetLocalHeroResponsesFromJsonUseCase(coroutineTestRule.testDispatcher, moshi)

    @Test
    fun `fields from json converts properly in LocalHeroResponsesItem`() = coroutineTestRule.runBlockingTest {
        val actual = LocalHeroResponsesItem("Fight", listOf(Response("https//...", "arrrr")))
        val json = """[
  {
    "responses": [
      {
        "audioUrl": "${actual.responses[0].audioUrl}",
        "title": "${actual.responses[0].title}"
      }
    ],
    "category": "${actual.category}"
  }
]
        """.trimIndent()
        val result = getLocalHeroResponsesFromJsonUseCase(json)
        assertThat(result[0].category).isEqualTo(actual.category)
        assertThat(result[0].responses[0].audioUrl).isEqualTo(actual.responses[0].audioUrl)
        assertThat(result[0].responses[0].title).isEqualTo(actual.responses[0].title)
    }

}