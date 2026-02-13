package n7.ad2.hero.page.internal.guides.domain.usecase

import kotlinx.coroutines.withContext
import n7.ad2.coroutines.DispatchersProvider
import n7.ad2.database_guides.internal.model.LocalGuide
import java.util.Locale
import javax.inject.Inject

private inline class HeroNameFormatted(val heroName: String)

class GetLocalGuideJsonUseCase @Inject constructor(private val dispatchers: DispatchersProvider) {

    companion object {
        private fun getHeroNameFormatted(heroName: String) = HeroNameFormatted(heroName.lowercase(Locale.ENGLISH).replace("_", "-").replace("'", "").replace("%20", "-").replace(" ", "-"))

        private fun getUrlForHeroPage(heroName: HeroNameFormatted) = "https://ru.dotabuff.com/heroes/${heroName.heroName}"
        private fun getUrlForHeroGuides(heroName: HeroNameFormatted) = "https://www.dotabuff.com/heroes/${heroName.heroName}/guides"
    }

    suspend operator fun invoke(heroName: String): List<LocalGuide> = withContext(dispatchers.Default) {
        val formattedHeroName = getHeroNameFormatted(heroName)
        val guidesUrl = getUrlForHeroGuides(formattedHeroName)

        // TODO: Implement actual web scraping from dotabuff
        // For now, return a mock guide structure
        val mockGuideJson = """
        {
            "hero": "$heroName",
            "guides": [
                {
                    "title": "Standard Build",
                    "items": ["item1", "item2", "item3"],
                    "skills": ["skill1", "skill2", "skill3"]
                }
            ]
        }
        """.trimIndent()

        listOf(
            LocalGuide(
                name = heroName,
                json = mockGuideJson,
                timestamp = System.currentTimeMillis(),
            ),
        )
    }
}
