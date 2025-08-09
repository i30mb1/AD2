package n7.ad2.parser.heroes

internal class Hero(
    val name: String,
    val folderName: String,
    val mainAttribute: String,
    val href: String,
    val heroType: String = "Unknown", // Core, Support, etc
    val attackType: String = "Unknown", // Melee, Ranged
    val complexity: Int = 1, // 1-3 complexity rating
    val roles: List<String> = emptyList(), // Carry, Support, Initiator, etc
)