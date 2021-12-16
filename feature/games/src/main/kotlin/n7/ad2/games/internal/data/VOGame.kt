package n7.ad2.games.internal.data

internal sealed class Players {
    object One : Players()
    object Two : Players()
}

internal sealed class VOGame(val name: String) {
    class SpellCost(name: String) : VOGame(name)
    class Apm(name: String) : VOGame(name)
    class CanYouBuyIt(name: String) : VOGame(name)
}