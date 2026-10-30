package n7.ad2.heroes.domain.internal.usecase

import android.app.Application
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.serialization.json.Json
import n7.ad2.AppInformation
import n7.ad2.coroutines.DispatchersProvider
import n7.ad2.heroes.domain.internal.data.model.AbilityJson
import n7.ad2.heroes.domain.internal.data.model.HeroDescriptionJson
import n7.ad2.heroes.domain.internal.data.model.PatchNoteJson
import n7.ad2.heroes.domain.internal.data.model.WikiSectionJson
import n7.ad2.heroes.domain.model.Ability
import n7.ad2.heroes.domain.model.AbilityBehavior
import n7.ad2.heroes.domain.model.AbilityKind
import n7.ad2.heroes.domain.model.AbilityParam
import n7.ad2.heroes.domain.model.AbilityPatchNotes
import n7.ad2.heroes.domain.model.AttackType
import n7.ad2.heroes.domain.model.DamageType
import n7.ad2.heroes.domain.model.Dispellable
import n7.ad2.heroes.domain.model.Facet
import n7.ad2.heroes.domain.model.HeroAttribute
import n7.ad2.heroes.domain.model.HeroAttributes
import n7.ad2.heroes.domain.model.HeroDescription
import n7.ad2.heroes.domain.model.HeroRole
import n7.ad2.heroes.domain.model.HeroStats
import n7.ad2.heroes.domain.model.HeroWiki
import n7.ad2.heroes.domain.model.PatchChange
import n7.ad2.heroes.domain.model.PatchNote
import n7.ad2.heroes.domain.model.Playstyle
import n7.ad2.heroes.domain.model.Role
import n7.ad2.heroes.domain.model.SpellImmunity
import n7.ad2.heroes.domain.model.TalentLevel
import n7.ad2.heroes.domain.model.TargetTeam
import n7.ad2.heroes.domain.model.WikiEntry
import n7.ad2.heroes.domain.model.WikiNote
import n7.ad2.heroes.domain.model.WikiSection
import n7.ad2.heroes.domain.usecase.GetHeroDescriptionUseCase

internal class GetHeroDescriptionUseCaseImpl(
    private val application: Application,
    private val appInformation: AppInformation,
    private val dispatcher: DispatchersProvider,
) : GetHeroDescriptionUseCase {

    private val json = Json { ignoreUnknownKeys = true }

    override fun invoke(name: String): Flow<HeroDescription> = flow {
        val key = name.lowercase().replace(" ", "_")
        val locale = appInformation.appLocale.value
        val text = application.assets
            .open("heroes/$key/$locale/description.json")
            .bufferedReader()
            .use { reader -> reader.readText() }
        emit(json.decodeFromString<HeroDescriptionJson>(text).toHeroDescription())
    }.flowOn(dispatcher.IO)
}

/** Keys come from core/parser, unknown ones are skipped so new datafeed values never crash the screen. */
private inline fun <reified T : Enum<T>> String?.toEnum(): T? = this?.let { key -> enumValues<T>().firstOrNull { it.name.equals(key, ignoreCase = true) } }

private fun HeroDescriptionJson.toHeroDescription(): HeroDescription = HeroDescription(
    name = name,
    primaryAttribute = primaryAttribute.toEnum<HeroAttribute>() ?: HeroAttribute.UNIVERSAL,
    attackType = attackType.toEnum<AttackType>() ?: AttackType.MELEE,
    complexity = complexity,
    roles = roles.mapNotNull { role -> role.role.toEnum<Role>()?.let { HeroRole(it, role.level) } },
    short = short,
    bio = bio,
    attributes = HeroAttributes(
        strength = attributes.strength,
        strengthGain = attributes.strengthGain,
        agility = attributes.agility,
        agilityGain = attributes.agilityGain,
        intelligence = attributes.intelligence,
        intelligenceGain = attributes.intelligenceGain,
    ),
    stats = HeroStats(
        health = stats.health,
        healthRegen = stats.healthRegen,
        mana = stats.mana,
        manaRegen = stats.manaRegen,
        armor = stats.armor,
        magicResist = stats.magicResist,
        damageMin = stats.damageMin,
        damageMax = stats.damageMax,
        attackRate = stats.attackRate,
        attackRange = stats.attackRange,
        projectileSpeed = stats.projectileSpeed,
        moveSpeed = stats.moveSpeed,
        turnRate = stats.turnRate,
        visionDay = stats.visionDay,
        visionNight = stats.visionNight,
    ),
    abilities = abilities.map { ability -> ability.toAbility() },
    talents = talents.map { talent -> TalentLevel(talent.level, talent.left, talent.right) },
    facets = facets.map { facet -> Facet(facet.name, facet.description) },
    trivia = trivia,
    wiki = HeroWiki(
        recommendedItems = wiki.recommendedItems.toSections(),
        counters = wiki.counters.toSections(),
        playstyle = wiki.guide?.playstyle?.let { Playstyle(it.text, it.pros, it.cons) },
        tips = wiki.guide?.tips.orEmpty().toSections(),
    ),
    changes = changes.map { change ->
        PatchChange(
            patch = change.patch,
            timestampSeconds = change.timestamp,
            hero = change.hero.map { it.toPatchNote() },
            talents = change.talents.map { it.toPatchNote() },
            abilities = change.abilities.map { ability -> AbilityPatchNotes(ability.name, ability.notes.map { it.toPatchNote() }) },
        )
    },
)

private fun List<WikiSectionJson>.toSections() = map { section ->
    WikiSection(
        title = section.title,
        level = section.level,
        entries = section.entries.map { entry ->
            WikiEntry(entry.name, entry.icon?.let { "file:///android_asset/$it" }, entry.lines)
        },
    )
}

private fun PatchNoteJson.toPatchNote() = PatchNote(text, indent)

private fun AbilityJson.toAbility(): Ability = Ability(
    name = name,
    imageUrl = if (image.isEmpty()) "" else "file:///android_asset/$image",
    kind = kind.toEnum<AbilityKind>() ?: AbilityKind.BASIC,
    description = description,
    notes = notes,
    scepter = scepter,
    shard = shard,
    behavior = behavior.mapNotNull { it.toEnum<AbilityBehavior>() },
    targetTeam = targetTeam.toEnum<TargetTeam>(),
    damageType = damageType.toEnum<DamageType>(),
    spellImmunity = spellImmunity.toEnum<SpellImmunity>(),
    dispellable = dispellable.toEnum<Dispellable>(),
    cooldown = cooldown,
    mana = manaCost,
    healthCost = healthCost,
    goldCost = goldCost,
    castRange = castRange,
    castPoint = castPoint,
    channelTime = channelTime,
    duration = duration,
    params = params.map { param -> AbilityParam(param.label, param.value, param.talent, param.shard, param.scepter) },
    wikiNotes = wikiNotes.map { WikiNote(it.text, it.tag, it.indent) },
)
