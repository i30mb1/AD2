package n7.ad2.itempage.internal.domain

import n7.ad2.itempage.internal.domain.usecase.ItemTextParser
import n7.ad2.itempage.internal.domain.vo.VOFact
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class ItemTextParserTest {

    @Test
    fun `bonus is split into the number and the text`() {
        assertEquals("+10" to "к силе", ItemTextParser.splitBonus("+10 к силе"))
        assertEquals("+1.5" to "к восстановлению маны", ItemTextParser.splitBonus("+1.5 к восстановлению маны"))
        assertEquals("+20%" to "к сопротивлению магии", ItemTextParser.splitBonus("+20% к сопротивлению магии"))
        assertEquals("" to "Даёт невидимость", ItemTextParser.splitBonus("Даёт невидимость"))
    }

    @Test
    fun `ability name gives the kind`() {
        assertEquals("Активное" to "Avatar", ItemTextParser.splitAbilityName("Активное: Avatar"))
        assertEquals(null to "Blink", ItemTextParser.splitAbilityName("Blink"))
    }

    @Test
    fun `tooltip is split into text and facts`() {
        val html = "Применяет на владельца нормальное развеивание. Защищает: от эффектов.<br><br>Длительность: 9/8/7 сек.<br>" +
            "Тип развеивания: <span class=\"GameplayValues GameplayVariable\">нормальное</span>"
        val (text, facts) = ItemTextParser.splitDescription(html)
        assertEquals(listOf("Применяет на владельца нормальное развеивание. Защищает: от эффектов."), text)
        assertEquals(listOf(VOFact("Длительность", "9/8/7 сек."), VOFact("Тип развеивания", "нормальное")), facts)
    }

    @Test
    fun `long sentence with a colon is not a fact`() {
        assertNull(ItemTextParser.toFact("Атрибуты этого предмета дают следующие дополнительные бонусы: здоровье"))
        assertEquals(VOFact("Начальные заряды", "3"), ItemTextParser.toFact("Начальные заряды: 3"))
    }

    @Test
    fun `recommendation names heroes after the colon`() {
        val heroes = setOf("Lion", "Crystal Maiden", "Rubick")
        val line = "Герои поддержки могут инициировать: Lion, Crystal Maiden, Rubick"
        assertEquals("Герои поддержки могут инициировать" to listOf("Lion", "Crystal Maiden", "Rubick"), ItemTextParser.splitRecommendation(line, heroes::contains))
    }

    @Test
    fun `recommendation without heroes after the colon stays whole`() {
        val line = "Подходит всем: особенно в затяжных играх"
        assertEquals(line to emptyList<String>(), ItemTextParser.splitRecommendation(line) { false })
    }

    @Test
    fun `recipe cost is what is left after the components`() {
        assertEquals(1450, ItemTextParser.recipeCost(4050, listOf(1600, 1000)))
        assertNull(ItemTextParser.recipeCost(2600, listOf(1600, 1000)))
        assertNull(ItemTextParser.recipeCost(4050, listOf(1600, null)))
        assertNull(ItemTextParser.recipeCost(null, listOf(1600)))
    }
}
