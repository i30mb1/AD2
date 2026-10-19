package n7.ad2.hero.page.internal.info.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import java.text.DateFormat
import java.util.Date
import n7.ad2.core.ui.compose.AppTheme
import n7.ad2.feature.hero.page.ui.R
import n7.ad2.heroes.domain.model.PatchChange
import n7.ad2.heroes.domain.model.PatchNote
import n7.ad2.heroes.domain.model.Playstyle
import n7.ad2.heroes.domain.model.WikiArticle
import n7.ad2.heroes.domain.model.WikiEntry
import n7.ad2.heroes.domain.model.WikiNotes

private val prosColor = Color(0xFF6CC56A)
private val consColor = Color(0xFFE5655A)

/** Where the block comes from, with an "EN" mark when the wiki has no text in the app language. */
@Composable
internal fun WikiSourceLabel(isFallbackLanguage: Boolean, modifier: Modifier = Modifier) {
    Row(modifier, verticalAlignment = Alignment.CenterVertically) {
        Caption(stringResource(R.string.info_wiki_source))
        if (isFallbackLanguage) {
            HSpace(6.dp)
            LangBadge()
        }
    }
}

@Composable
private fun LangBadge() {
    Text(
        text = stringResource(R.string.info_lang_en),
        fontSize = 9.sp,
        fontWeight = FontWeight.Bold,
        color = AppTheme.color.textSecondaryColor,
        modifier = Modifier
            .border(1.dp, AppTheme.color.textSecondaryColor.copy(alpha = 0.6f), RoundedCornerShape(3.dp))
            .padding(horizontal = 4.dp, vertical = 1.dp),
    )
}

/** Shows the first [visible] items and a "Show all (N)" button for the rest. */
@Composable
internal fun <T> ExpandableItems(items: List<T>, visible: Int, item: @Composable (T) -> Unit) {
    var isExpanded by rememberSaveable(items.size) { mutableStateOf(false) }
    val shown = if (isExpanded || items.size <= visible + 1) items else items.take(visible)
    shown.forEach { item(it) }
    if (shown.size < items.size) {
        Text(
            text = stringResource(R.string.info_show_all, items.size),
            style = AppTheme.style.H5,
            fontWeight = FontWeight.Bold,
            color = AppTheme.color.primary,
            modifier = Modifier
                .padding(top = 6.dp)
                .clip(AppTheme.shape.small)
                .clickable { isExpanded = true }
                .padding(vertical = 4.dp),
        )
    }
}

// ===== patch changes =====

@Composable
internal fun ChangesCard(changes: List<PatchChange>, abilityIcons: Map<String, String>) {
    InfoCard(title = stringResource(R.string.info_changes)) {
        changes.forEachIndexed { index, change ->
            if (index > 0) InfoDivider(Modifier.padding(vertical = 12.dp))
            PatchHeader(change)
            if (change.hero.isNotEmpty()) {
                VSpace(8.dp)
                Caption(stringResource(R.string.info_changes_hero))
                PatchNotes(change.hero)
            }
            change.abilities.forEach { ability ->
                VSpace(8.dp)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    abilityIcons[ability.abilityName]?.let { icon ->
                        AsyncImage(model = icon, contentDescription = null, modifier = Modifier.size(18.dp).clip(AppTheme.shape.small))
                        HSpace(6.dp)
                    }
                    Text(ability.abilityName, style = AppTheme.style.H5, fontWeight = FontWeight.Bold, color = AppTheme.color.textColor)
                }
                PatchNotes(ability.notes)
            }
            if (change.talents.isNotEmpty()) {
                VSpace(8.dp)
                Caption(stringResource(R.string.info_talents))
                PatchNotes(change.talents)
            }
        }
    }
}

@Composable
internal fun PatchHeader(change: PatchChange) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = change.patch,
            style = AppTheme.style.H5,
            fontWeight = FontWeight.Bold,
            color = AppTheme.color.textColor,
            modifier = Modifier
                .clip(AppTheme.shape.small)
                .background(AppTheme.color.primary.copy(alpha = 0.25f))
                .padding(horizontal = 8.dp, vertical = 2.dp),
        )
        if (change.timestampSeconds > 0) {
            HSpace(8.dp)
            Text(
                text = DateFormat.getDateInstance(DateFormat.MEDIUM).format(Date(change.timestampSeconds * 1000)),
                style = AppTheme.style.body,
                color = AppTheme.color.textSecondaryColor,
            )
        }
    }
}

@Composable
internal fun PatchNotes(notes: List<PatchNote>) {
    notes.forEach { note ->
        Box(Modifier.padding(start = (note.indent * 16).dp)) { BulletText(note.text) }
    }
}

// ===== recommended items =====

@Composable
internal fun RecommendedItemsCard(article: WikiArticle) {
    InfoCard(title = stringResource(R.string.info_items)) {
        WikiSourceLabel(article.isFallbackLanguage)
        article.sections.forEach { phase ->
            VSpace(12.dp)
            Caption(phase.title, color = AppTheme.color.textColor)
            VSpace(6.dp)
            phase.entries.forEach { entry -> EntryRow(entry, iconSize = 34) }
        }
    }
}

// ===== counters =====

@Composable
internal fun CountersCard(article: WikiArticle) {
    InfoCard(title = stringResource(R.string.info_counters)) {
        WikiSourceLabel(article.isFallbackLanguage)
        article.sections.forEach { section ->
            VSpace(if (section.level <= 2) 16.dp else 10.dp)
            if (section.level <= 2) {
                Text(section.title, style = AppTheme.style.H4, fontWeight = FontWeight.Bold, color = AppTheme.color.primary)
            } else {
                Caption(section.title, color = AppTheme.color.textColor)
            }
            VSpace(6.dp)
            ExpandableItems(section.entries, visible = 4) { entry -> EntryRow(entry, iconSize = 40) }
        }
    }
}

/** Hero or item icon with its name and the lines about it, or plain bullet lines when there is no name. */
@Composable
private fun EntryRow(entry: WikiEntry, iconSize: Int) {
    val name = entry.name
    if (name == null) {
        entry.lines.forEach { BulletText(it, color = AppTheme.color.textColor) }
        return
    }
    Row(Modifier.padding(vertical = 6.dp)) {
        if (entry.iconUrl != null) {
            AsyncImage(
                model = entry.iconUrl,
                contentDescription = name,
                placeholder = painterResource(n7.ad2.core.ui.R.drawable.square_placeholder),
                error = painterResource(n7.ad2.core.ui.R.drawable.square_error_placeholder),
                modifier = Modifier
                    .size(iconSize.dp)
                    .clip(AppTheme.shape.medium),
            )
            HSpace(10.dp)
        }
        Column(Modifier.weight(1f)) {
            Text(name, style = AppTheme.style.H5, fontWeight = FontWeight.Bold, color = AppTheme.color.textColor)
            entry.lines.forEach { line ->
                VSpace(2.dp)
                RichText(line, color = AppTheme.color.textSecondaryColor)
            }
        }
    }
}

// ===== strategy =====

@Composable
internal fun StrategyCard(playstyle: Playstyle?, tips: WikiArticle?) {
    InfoCard(title = stringResource(R.string.info_strategy)) {
        if (playstyle != null) {
            WikiSourceLabel(playstyle.isFallbackLanguage)
            VSpace(8.dp)
            RichText(playstyle.text)
            if (playstyle.pros.isNotEmpty() || playstyle.cons.isNotEmpty()) {
                VSpace(12.dp)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    ProsCons(stringResource(R.string.info_pros), "+", playstyle.pros, prosColor, Modifier.weight(1f))
                    ProsCons(stringResource(R.string.info_cons), "−", playstyle.cons, consColor, Modifier.weight(1f))
                }
            }
        }
        if (tips != null) {
            if (playstyle == null || tips.isFallbackLanguage != playstyle.isFallbackLanguage) {
                VSpace(if (playstyle == null) 0.dp else 16.dp)
                WikiSourceLabel(tips.isFallbackLanguage)
            }
            tips.sections.forEach { section ->
                VSpace(14.dp)
                Text(section.title, style = AppTheme.style.H5, fontWeight = FontWeight.Bold, color = AppTheme.color.textColor)
                VSpace(4.dp)
                val lines = section.entries.flatMap { it.lines }
                ExpandableItems(lines, visible = 3) { line -> BulletText(line) }
            }
        }
    }
}

@Composable
private fun ProsCons(title: String, sign: String, items: List<String>, color: Color, modifier: Modifier) {
    Column(
        modifier = modifier
            .clip(AppTheme.shape.medium)
            .background(color.copy(alpha = 0.08f))
            .padding(10.dp),
    ) {
        Caption(title, color = color)
        items.forEach { item ->
            Row(Modifier.padding(top = 6.dp)) {
                Text(sign, style = AppTheme.style.H5, fontWeight = FontWeight.Bold, color = color)
                HSpace(6.dp)
                Text(item, style = AppTheme.style.body.copy(lineHeight = 16.sp), color = AppTheme.color.textColor)
            }
        }
    }
}

// ===== ability parts =====

/** Wiki notes of the selected ability: mechanics, interactions, upgrade details. */
@Composable
internal fun AbilityWikiNotes(notes: WikiNotes) {
    Text(stringResource(R.string.info_wiki_hint), style = AppTheme.style.body, color = AppTheme.color.textSecondaryColor.copy(alpha = 0.7f))
    VSpace(6.dp)
    ExpandableItems(notes.notes, visible = 5) { note ->
        Column(Modifier.padding(start = (note.indent * 16).dp)) {
            note.tag?.let { tag ->
                Text(
                    text = tag.uppercase(),
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color = InfoColors.aghanim,
                    modifier = Modifier.padding(top = 4.dp),
                )
            }
            BulletText(note.text)
        }
    }
}

@Composable
internal fun AbilityChanges(changes: List<Pair<PatchChange, List<PatchNote>>>) {
    changes.forEachIndexed { index, (change, notes) ->
        if (index > 0) VSpace(8.dp)
        PatchHeader(change)
        PatchNotes(notes)
    }
}
