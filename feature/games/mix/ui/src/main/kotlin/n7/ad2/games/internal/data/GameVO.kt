package n7.ad2.games.internal.data

import n7.ad2.Resources
import n7.ad2.feature.games.ui.R

internal sealed class Players {
    object One : Players()
    object Two : Players()
}

internal sealed class GameVO(val title: String, val backgroundImage: Int) {
    class GuessSkillMana(resources: Resources) : GameVO(resources.getString(R.string.spell_cost), R.drawable.background_guess_skill)
    class Apm(resources: Resources) : GameVO(resources.getString(R.string.apm), R.drawable.background_apm)
    class CanYouBuyIt(resources: Resources) : GameVO(resources.getString(R.string.can_you_buy_it), R.drawable.background_guess_skill)
    class XO(resources: Resources) : GameVO(resources.getString(R.string.xo), R.drawable.background_guess_skill)
}
