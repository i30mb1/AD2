package n7.ad2.games.internal.data

import n7.ad2.Resources
import n7.ad2.feature.games.mix.ui.R
import n7.ad2.games.internal.games.skillmp.SkillGameViewModel

internal sealed class Players {
    object One : Players()
    object Two : Players()
}

/** [quizId] - id игры в рекордах и комнате для викторин с режимами Solo/Versus/Coop, null у остальных */
internal sealed class GameVO(val title: String, val backgroundImage: Int, val quizId: String? = null) {
    class GuessSkillMana(resources: Resources) : GameVO(resources.getString(R.string.spell_cost), R.drawable.background_guess_skill, SkillGameViewModel.GAME_ID)
    class GuessHero(resources: Resources) : GameVO(resources.getString(R.string.guess_hero), R.drawable.background_apm, SkillGameViewModel.HERO_GAME_ID)
    class HigherLower(resources: Resources) : GameVO(resources.getString(R.string.higher_lower), R.drawable.background_guess_skill, SkillGameViewModel.COMPARE_GAME_ID)
    class Apm(resources: Resources) : GameVO(resources.getString(R.string.apm), R.drawable.background_apm)
    class CanYouBuyIt(resources: Resources) : GameVO(resources.getString(R.string.can_you_buy_it), R.drawable.background_guess_skill)
    class XO(resources: Resources) : GameVO(resources.getString(R.string.xo), R.drawable.background_guess_skill)
}
