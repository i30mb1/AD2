package n7.ad2.games.internal.data

internal sealed class Players {
    object One : Players()
    object Two : Players()
}

 sealed class GameVO(val title: String) {
     class SpellCost(name: String, val backgroundImage: Int) : GameVO(name)
     class Apm(name: String) : GameVO(name)
     class CanYouBuyIt(name: String) : GameVO(name)
 }