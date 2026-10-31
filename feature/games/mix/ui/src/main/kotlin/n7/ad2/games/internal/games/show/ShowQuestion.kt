package n7.ad2.games.internal.games.show

import android.app.Application
import javax.inject.Inject
import kotlin.math.roundToInt
import kotlin.random.Random
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import n7.ad2.coroutines.DispatchersProvider
import n7.ad2.feature.games.mix.ui.R
import n7.ad2.heroes.domain.model.Ability
import n7.ad2.heroes.domain.model.AbilityKind
import n7.ad2.heroes.domain.model.Hero
import n7.ad2.heroes.domain.model.HeroStats
import n7.ad2.heroes.domain.usecase.GetHeroDescriptionUseCase
import n7.ad2.heroes.domain.usecase.GetHeroesUseCase
import org.json.JSONArray

/** An answer card: a mana cost or a hero */
internal data class ShowOption(val label: String, val imageUrl: String?, val isRight: Boolean)

/** A stat for «Кто сильнее?»: the question and how to get the value at level 1 */
internal enum class CompareStat(val question: Int, val value: (HeroStats) -> Double, val format: (Double) -> String = { it.roundToInt().toString() }) {
    HEALTH(R.string.compare_health, { it.health }),
    MANA(R.string.compare_mana, { it.mana }),
    ARMOR(R.string.compare_armor, { it.armor }, { "%.1f".format(it) }),
    DAMAGE(R.string.compare_damage, { (it.damageMin + it.damageMax) / 2 }),
    MOVE_SPEED(R.string.compare_move_speed, { it.moveSpeed }),
    ATTACK_RANGE(R.string.compare_attack_range, { it.attackRange }),
}

internal sealed interface ShowQuestion {
    val kind: ShowKind

    /** One of four: the mana cost of a spell at [spellLevel], the hero of a spell, the hero of a voice line */
    data class Pick(
        override val kind: ShowKind,
        val spellImage: String?,
        val spellLevel: Int,
        val voice: VoiceLine?,
        val options: List<ShowOption>,
    ) : ShowQuestion

    /** One of two heroes: who has more of [stat] at level 1; the values show after the answer */
    data class Compare(val stat: CompareStat, val options: List<ShowOption>, val values: List<String>) : ShowQuestion {
        override val kind: ShowKind get() = ShowKind.COMPARE
    }

    /** The components of an item */
    data class Build(val question: BuildQuestion) : ShowQuestion {
        override val kind: ShowKind get() = ShowKind.BUILD
    }
}

/** Answers to pick from: the build has tiles instead */
internal val ShowQuestion.options: List<ShowOption>
    get() = when (this) {
        is ShowQuestion.Pick -> options
        is ShowQuestion.Compare -> options
        is ShowQuestion.Build -> emptyList()
    }

/**
 * Questions of the show. A question is built only from the random it gets: everyone in the room gets the same one.
 * A hero without a fitting spell or line throws, the caller tries the next random.
 */
internal class ShowQuestions @Inject constructor(
    private val application: Application,
    private val getHeroesUseCase: GetHeroesUseCase,
    private val getHeroDescriptionUseCase: GetHeroDescriptionUseCase,
    private val itemRecipes: ItemRecipes,
    private val dispatchers: DispatchersProvider,
) {

    suspend fun load(kind: ShowKind, random: Random): ShowQuestion = withContext(dispatchers.IO) {
        val heroes = getHeroesUseCase().first().sortedBy { it.name }
        when (kind) {
            ShowKind.MANA -> mana(heroes, random)
            ShowKind.HERO -> heroBySpell(heroes, random)
            ShowKind.VOICE -> heroByVoice(heroes, random)
            ShowKind.COMPARE -> compare(heroes, random)
            ShowKind.BUILD -> ShowQuestion.Build(createBuildQuestion(itemRecipes.all(), random))
        }
    }

    private suspend fun mana(heroes: List<Hero>, random: Random): ShowQuestion {
        val hero = heroes.random(random)
        val spell = abilities(hero).filter { it.imageUrl.isNotEmpty() && it.manaCosts() != null }.random(random)
        val costs = requireNotNull(spell.manaCosts())
        val level = random.nextInt(costs.size)
        val right = costs[level]
        val options = manaOptions(right, random).map { ShowOption(it.toString(), imageUrl = null, isRight = it == right) }
        return ShowQuestion.Pick(ShowKind.MANA, spell.imageUrl, spellLevel = level + 1, voice = null, options)
    }

    private suspend fun heroBySpell(heroes: List<Hero>, random: Random): ShowQuestion {
        val hero = heroes.random(random)
        val spell = abilities(hero).filter { it.imageUrl.isNotEmpty() && (it.kind == AbilityKind.BASIC || it.kind == AbilityKind.ULTIMATE) }.random(random)
        return ShowQuestion.Pick(ShowKind.HERO, spell.imageUrl, spellLevel = 0, voice = null, heroOptions(heroes, hero, random))
    }

    private fun heroByVoice(heroes: List<Hero>, random: Random): ShowQuestion {
        val hero = heroes.random(random)
        val line = guessableLines(hero.name, voiceLines(hero.name)).random(random)
        return ShowQuestion.Pick(ShowKind.VOICE, spellImage = null, spellLevel = 0, voice = line, heroOptions(heroes, hero, random))
    }

    private suspend fun compare(heroes: List<Hero>, random: Random): ShowQuestion {
        val stat = CompareStat.entries.random(random)
        repeat(MAX_PAIRS) {
            val (left, right) = heroes.shuffled(random).take(2)
            val leftValue = stat.value(getHeroDescriptionUseCase(left.name).first().stats)
            val rightValue = stat.value(getHeroDescriptionUseCase(right.name).first().stats)
            // equal values have no right answer
            if (stat.format(leftValue) != stat.format(rightValue)) {
                val leftWins = leftValue > rightValue
                return ShowQuestion.Compare(
                    stat = stat,
                    options = listOf(ShowOption(left.name, left.avatarUrl, leftWins), ShowOption(right.name, right.avatarUrl, !leftWins)),
                    values = listOf(stat.format(leftValue), stat.format(rightValue)),
                )
            }
        }
        error("no pair with different ${stat.name}")
    }

    private fun heroOptions(heroes: List<Hero>, hero: Hero, random: Random): List<ShowOption> =
        (heroes.filter { it.name != hero.name }.shuffled(random).take(OPTIONS - 1) + hero)
            .shuffled(random)
            .map { ShowOption(it.name, it.avatarUrl, isRight = it.name == hero.name) }

    private suspend fun abilities(hero: Hero): List<Ability> = getHeroDescriptionUseCase(hero.name).first().abilities

    /** The hero's lines from the database assets; the sound is the same in every language */
    private fun voiceLines(heroName: String): List<VoiceLine> {
        val folder = heroName.lowercase().replace(" ", "_")
        val json = application.assets.open("heroes/$folder/en/responses.json").bufferedReader().use { it.readText() }
        val categories = JSONArray(json)
        return (0 until categories.length()).flatMap { index ->
            val responses = categories.getJSONObject(index).optJSONArray("responses") ?: JSONArray()
            (0 until responses.length()).map { line ->
                val response = responses.getJSONObject(line)
                VoiceLine(response.optString("title"), response.optString("audio_url"))
            }
        }
    }

    private fun Ability.manaCosts(): List<Int>? =
        mana?.split("/")?.map { it.trim().toIntOrNull() ?: return null }?.takeIf { costs -> costs.isNotEmpty() && costs.all { it > 0 } }

    private companion object {
        const val OPTIONS = 4
        const val MAX_PAIRS = 10
    }
}
