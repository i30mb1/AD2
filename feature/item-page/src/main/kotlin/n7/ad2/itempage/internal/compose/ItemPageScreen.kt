package n7.ad2.itempage.internal.compose

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.windowInsetsTopHeight
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import n7.ad2.core.ui.compose.AppTheme
import n7.ad2.core.ui.compose.view.ErrorScreen
import n7.ad2.core.ui.compose.view.LoadingScreen
import n7.ad2.feature.item.page.R
import n7.ad2.itempage.internal.ItemInfoViewModel
import n7.ad2.itempage.internal.domain.vo.VOAbility
import n7.ad2.itempage.internal.domain.vo.VOBonus
import n7.ad2.itempage.internal.domain.vo.VOFact
import n7.ad2.itempage.internal.domain.vo.VOHero
import n7.ad2.itempage.internal.domain.vo.VOItemLink
import n7.ad2.itempage.internal.domain.vo.VOItemPage
import n7.ad2.itempage.internal.domain.vo.VORecipe
import n7.ad2.itempage.internal.domain.vo.VORecommendation
import n7.ad2.itempage.internal.domain.vo.VOSection
import n7.ad2.itempage.internal.domain.vo.VOSectionKind
import java.text.NumberFormat

private val headerTopColor = Color(0xFF3A3020)
private val goldColor = Color(0xFFE8C25A)
private val bonusColor = Color(0xFF8BC34A)
private val manaColor = Color(0xFF3D7BD8)
private val lineColor = Color(0xFF3A3D43)
private val itemTileWidth = 58.dp
private val itemTileHeight = 42.dp

@Composable
internal fun ItemPageScreen(
    state: ItemInfoViewModel.State,
    playingUrl: String?,
    onBackClicked: () -> Unit,
    onRetryClicked: () -> Unit,
    onItemClicked: (key: String) -> Unit,
    onHeroClicked: (name: String) -> Unit,
    onPlayClicked: (url: String) -> Unit,
) {
    when (state) {
        is ItemInfoViewModel.State.Loading -> LoadingScreen()
        is ItemInfoViewModel.State.Error -> ErrorScreen(state.error, onRetryClicked)
        is ItemInfoViewModel.State.Content -> ItemPage(state.page, playingUrl, onBackClicked, onItemClicked, onHeroClicked, onPlayClicked)
    }
}

@Composable
private fun ItemPage(
    page: VOItemPage,
    playingUrl: String?,
    onBackClicked: () -> Unit,
    onItemClicked: (key: String) -> Unit,
    onHeroClicked: (name: String) -> Unit,
    onPlayClicked: (url: String) -> Unit,
) {
    val insetsBottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
    val listState = rememberLazyListState()
    // once the header is scrolled away the cards would run under the clock, a scrim keeps the status bar readable
    val isHeaderScrolledAway by remember { derivedStateOf { listState.firstVisibleItemIndex > 0 } }
    Box(Modifier.fillMaxSize()) {
        LazyColumn(
            state = listState,
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(bottom = insetsBottom + 24.dp),
        ) {
            item(key = "header") { Header(page, onBackClicked) }
            if (page.bonuses.isNotEmpty()) item(key = "bonuses") { Bonuses(page.bonuses) }
            page.recipe?.let { recipe -> item(key = "recipe") { Recipe(recipe, page, onItemClicked) } }
            if (page.upgrades.isNotEmpty()) item(key = "upgrades") { Upgrades(page.upgrades, onItemClicked) }
            items(page.abilities, key = { "ability_${it.kind}_${it.name}" }) { ability -> AbilityCard(ability, playingUrl, onPlayClicked) }
            if (page.properties.isNotEmpty()) item(key = "properties") { Properties(page.properties) }
            if (page.recommendations.isNotEmpty()) item(key = "recommended") { Recommendations(page.recommendations, onHeroClicked) }
            items(page.sections, key = { it.kind }) { section -> Section(section) }
        }
        AnimatedVisibility(visible = isHeaderScrolledAway, enter = fadeIn(), exit = fadeOut()) {
            Box(
                Modifier
                    .fillMaxWidth()
                    .windowInsetsTopHeight(WindowInsets.statusBars)
                    .background(AppTheme.color.background.copy(alpha = 0.92f)),
            )
        }
    }
}

@Composable
private fun Card(modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    Column(
        modifier = modifier
            .padding(horizontal = 12.dp)
            .fillMaxWidth()
            .clip(AppTheme.shape.medium)
            .background(AppTheme.color.surface)
            .padding(12.dp),
    ) {
        content()
    }
}

@Composable
private fun Caption(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text.uppercase(),
        style = AppTheme.style.body,
        fontWeight = FontWeight.Bold,
        letterSpacing = 0.8.sp,
        color = AppTheme.color.textSecondaryColor,
        modifier = modifier,
    )
}

@Composable
private fun Gold(cost: Int?, modifier: Modifier = Modifier, fontSize: Int = 11) {
    if (cost == null) return
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        Box(
            Modifier
                .size((fontSize - 2).dp)
                .clip(CircleShape)
                .background(goldColor),
        )
        Text(
            text = NumberFormat.getIntegerInstance().format(cost),
            fontSize = fontSize.sp,
            fontWeight = FontWeight.Bold,
            color = goldColor,
            modifier = Modifier.padding(start = 4.dp),
        )
    }
}

@Composable
private fun Header(page: VOItemPage, onBackClicked: () -> Unit) {
    val insetsTop = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Brush.verticalGradient(listOf(headerTopColor, AppTheme.color.background)))
            .padding(top = insetsTop),
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = stringResource(R.string.desc_back),
            tint = AppTheme.color.textColor,
            modifier = Modifier
                .padding(4.dp)
                .size(44.dp)
                .clip(CircleShape)
                .clickable(role = Role.Button, onClick = onBackClicked)
                .padding(10.dp),
        )
        Row(
            modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 4.dp, bottom = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            AsyncImage(
                model = page.imageUrl,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                placeholder = painterResource(R.drawable.item_placeholder),
                modifier = Modifier
                    .size(width = 110.dp, height = 80.dp)
                    .shadow(12.dp, AppTheme.shape.medium)
                    .clip(AppTheme.shape.medium),
            )
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 14.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                Text(text = page.title, fontSize = 22.sp, lineHeight = 26.sp, fontWeight = FontWeight.Bold, color = AppTheme.color.textColor)
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Gold(page.cost, fontSize = 14)
                    page.boughtFrom?.let { shop -> Tag(shop) }
                }
            }
        }
        page.description?.let { description ->
            Text(
                text = description,
                fontSize = 13.sp,
                lineHeight = 18.sp,
                fontStyle = FontStyle.Italic,
                color = AppTheme.color.textSecondaryColor.copy(alpha = 0.75f),
                modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 6.dp),
            )
        }
    }
}

@Composable
private fun Tag(text: String) {
    Text(
        text = text,
        fontSize = 11.sp,
        color = AppTheme.color.textSecondaryColor,
        modifier = Modifier
            .border(1.dp, lineColor, AppTheme.shape.small)
            .padding(horizontal = 6.dp, vertical = 2.dp),
    )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun Bonuses(bonuses: List<VOBonus>) {
    Card {
        FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            bonuses.forEach { bonus ->
                Text(
                    text = buildAnnotatedString {
                        if (bonus.value.isNotEmpty()) {
                            withStyle(SpanStyle(color = bonusColor, fontWeight = FontWeight.Bold)) { append(bonus.value) }
                            append(' ')
                        }
                        append(bonus.text)
                    },
                    fontSize = 13.sp,
                    color = AppTheme.color.textColor,
                    modifier = Modifier
                        .clip(RoundedCornerShape(14.dp))
                        .background(AppTheme.color.background)
                        .padding(horizontal = 10.dp, vertical = 5.dp),
                )
            }
        }
    }
}

@Composable
private fun ItemTile(link: VOItemLink?, cost: Int?, onClick: (() -> Unit)?, isResult: Boolean = false) {
    Column(
        modifier = Modifier
            .clip(AppTheme.shape.small)
            .then(if (onClick != null) Modifier.clickable(role = Role.Button, onClick = onClick) else Modifier),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        if (link != null) {
            AsyncImage(
                model = link.imageUrl,
                contentDescription = link.name,
                contentScale = ContentScale.Crop,
                placeholder = painterResource(R.drawable.item_placeholder),
                modifier = Modifier
                    .size(itemTileWidth, itemTileHeight)
                    .clip(AppTheme.shape.small)
                    .then(if (isResult) Modifier.border(2.dp, AppTheme.color.primary, AppTheme.shape.small) else Modifier),
            )
        } else {
            Box(
                modifier = Modifier
                    .size(itemTileWidth, itemTileHeight)
                    .clip(AppTheme.shape.small)
                    .background(lineColor),
                contentAlignment = Alignment.Center,
            ) {
                Text(text = stringResource(R.string.item_recipe), fontSize = 10.sp, color = AppTheme.color.textSecondaryColor)
            }
        }
        Gold(cost)
    }
}

@Composable
private fun Operator(text: String) {
    Text(
        text = text,
        fontSize = 18.sp,
        color = AppTheme.color.textSecondaryColor,
        modifier = Modifier
            .height(itemTileHeight)
            .padding(top = 8.dp),
    )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun Recipe(recipe: VORecipe, page: VOItemPage, onItemClicked: (key: String) -> Unit) {
    Card {
        Caption(stringResource(R.string.item_build))
        FlowRow(
            modifier = Modifier.padding(top = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            recipe.parts.forEachIndexed { index, part ->
                if (index > 0) Operator("+")
                ItemTile(part, part.cost, onClick = { onItemClicked(part.key) })
            }
            recipe.recipeCost?.let { cost ->
                Operator("+")
                ItemTile(null, cost, onClick = null)
            }
            Operator("=")
            ItemTile(VOItemLink("", page.title, page.imageUrl, page.cost), page.cost, onClick = null, isResult = true)
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun Upgrades(upgrades: List<VOItemLink>, onItemClicked: (key: String) -> Unit) {
    Card {
        Caption(stringResource(R.string.upgrade_in))
        FlowRow(
            modifier = Modifier.padding(top = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            upgrades.forEach { upgrade ->
                Column(
                    modifier = Modifier
                        .widthIn(max = 76.dp)
                        .clip(AppTheme.shape.small)
                        .clickable(role = Role.Button) { onItemClicked(upgrade.key) },
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    AsyncImage(
                        model = upgrade.imageUrl,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        placeholder = painterResource(R.drawable.item_placeholder),
                        modifier = Modifier
                            .size(itemTileWidth, itemTileHeight)
                            .clip(AppTheme.shape.small),
                    )
                    // two lines for every name, so the prices of a row stay on one line
                    Text(text = upgrade.name, fontSize = 11.sp, lineHeight = 13.sp, color = AppTheme.color.textSecondaryColor, textAlign = TextAlign.Center, minLines = 2, maxLines = 2)
                    Gold(upgrade.cost)
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun AbilityCard(ability: VOAbility, playingUrl: String?, onPlayClicked: (url: String) -> Unit) {
    var areNotesOpen by rememberSaveable(ability.name) { mutableStateOf(false) }
    Card {
        Row(verticalAlignment = Alignment.CenterVertically) {
            ability.kind?.let { kind ->
                val isPassive = kind.startsWith("Пассив", ignoreCase = true) || kind.startsWith("Passive", ignoreCase = true)
                Text(
                    text = kind.uppercase(),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.6.sp,
                    color = Color.White,
                    modifier = Modifier
                        .padding(end = 8.dp)
                        .clip(AppTheme.shape.small)
                        .background(if (isPassive) lineColor else AppTheme.color.primary)
                        .padding(horizontal = 6.dp, vertical = 2.dp),
                )
            }
            Text(
                text = ability.name,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = AppTheme.color.textColor,
                modifier = Modifier.weight(1f),
            )
            ability.audioUrl?.let { url ->
                val isPlaying = playingUrl == url
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(AppTheme.color.background)
                        .clickable(role = Role.Button, onClickLabel = stringResource(R.string.desc_play_sound)) { onPlayClicked(url) },
                    contentAlignment = Alignment.Center,
                ) {
                    // the core icon set has no "stop", a square says it as well
                    if (isPlaying) {
                        Box(
                            Modifier
                                .size(10.dp)
                                .clip(RoundedCornerShape(2.dp))
                                .background(AppTheme.color.primary),
                        )
                    } else {
                        Icon(Icons.Default.PlayArrow, contentDescription = stringResource(R.string.desc_play_sound), tint = AppTheme.color.primary, modifier = Modifier.size(18.dp))
                    }
                }
            }
        }
        if (ability.cooldown != null || ability.mana != null || ability.params.isNotEmpty()) {
            FlowRow(
                modifier = Modifier.padding(top = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                ability.cooldown?.let { cooldown -> Pill(stringResource(R.string.item_cooldown, cooldown), dotColor = AppTheme.color.textSecondaryColor) }
                ability.mana?.let { mana -> Pill(mana, dotColor = manaColor) }
                ability.params.forEach { param -> Pill(param, dotColor = null) }
            }
        }
        ability.effects.forEach { effect ->
            Text(text = effect, fontSize = 13.sp, color = bonusColor, modifier = Modifier.padding(top = 8.dp))
        }
        ability.text.forEach { paragraph ->
            Text(text = paragraph, fontSize = 14.sp, lineHeight = 20.sp, color = AppTheme.color.textColor.copy(alpha = 0.87f), modifier = Modifier.padding(top = 10.dp))
        }
        Facts(ability.facts, Modifier.padding(top = 4.dp))
        if (ability.notes.isNotEmpty()) {
            Text(
                text = if (areNotesOpen) stringResource(R.string.item_hide_notes) else stringResource(R.string.item_notes, ability.notes.size),
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = AppTheme.color.primary,
                modifier = Modifier
                    .padding(top = 10.dp)
                    .clip(AppTheme.shape.small)
                    .clickable(role = Role.Button) { areNotesOpen = !areNotesOpen }
                    .padding(vertical = 2.dp),
            )
            if (areNotesOpen) Bullets(ability.notes, Modifier.padding(top = 6.dp))
        }
    }
}

@Composable
private fun Pill(text: String, dotColor: Color?) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .then(if (dotColor != null) Modifier.background(AppTheme.color.background) else Modifier.border(1.dp, lineColor, RoundedCornerShape(12.dp)))
            .padding(horizontal = 8.dp, vertical = 3.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (dotColor != null) {
            Box(
                Modifier
                    .padding(end = 5.dp)
                    .size(8.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(dotColor),
            )
        }
        Text(text = text, fontSize = 12.sp, color = if (dotColor != null) AppTheme.color.textColor else AppTheme.color.textSecondaryColor)
    }
}

@Composable
private fun Facts(facts: List<VOFact>, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        facts.forEach { fact ->
            Divider(color = lineColor, modifier = Modifier.padding(top = 6.dp))
            Row(modifier = Modifier.padding(top = 6.dp)) {
                Text(text = fact.key, fontSize = 13.sp, color = AppTheme.color.textSecondaryColor, modifier = Modifier.weight(1f))
                Text(
                    text = fact.value,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = AppTheme.color.textColor,
                    textAlign = TextAlign.End,
                    modifier = Modifier.padding(start = 12.dp),
                )
            }
        }
    }
}

@Composable
private fun Properties(properties: List<VOFact>) {
    Card {
        Caption(stringResource(R.string.properties))
        Facts(properties, Modifier.padding(top = 4.dp))
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun Recommendations(recommendations: List<VORecommendation>, onHeroClicked: (name: String) -> Unit) {
    Card {
        Caption(stringResource(R.string.recommended_for))
        recommendations.forEach { recommendation ->
            Text(
                text = recommendation.text,
                fontSize = 13.sp,
                lineHeight = 18.sp,
                color = AppTheme.color.textSecondaryColor,
                modifier = Modifier.padding(top = 12.dp),
            )
            if (recommendation.heroes.isNotEmpty()) {
                FlowRow(
                    modifier = Modifier.padding(top = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    recommendation.heroes.forEach { hero -> HeroChip(hero, onHeroClicked) }
                }
            }
        }
    }
}

@Composable
private fun HeroChip(hero: VOHero, onHeroClicked: (name: String) -> Unit) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(AppTheme.color.background)
            .clickable(role = Role.Button) { onHeroClicked(hero.name) }
            .padding(start = 3.dp, end = 8.dp, top = 3.dp, bottom = 3.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        AsyncImage(
            model = hero.imageUrl,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(18.dp)
                .clip(CircleShape),
        )
        Text(text = hero.name, fontSize = 12.sp, color = AppTheme.color.textColor, modifier = Modifier.padding(start = 5.dp))
    }
}

@Composable
private fun Section(section: VOSection) {
    var isOpen by rememberSaveable(section.kind) { mutableStateOf(false) }
    val title = when (section.kind) {
        VOSectionKind.TIPS -> R.string.tips
        VOSectionKind.ADDITIONAL -> R.string.additional_information
        VOSectionKind.LORE -> R.string.lore
        VOSectionKind.TRIVIA -> R.string.trivia
    }
    Column(
        modifier = Modifier
            .padding(horizontal = 12.dp)
            .fillMaxWidth()
            .clip(AppTheme.shape.medium)
            .background(AppTheme.color.surface),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { isOpen = !isOpen }
                .padding(horizontal = 12.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Caption(stringResource(title), Modifier.weight(1f))
            Text(text = section.items.size.toString(), fontSize = 12.sp, color = AppTheme.color.textSecondaryColor)
            Icon(
                imageVector = Icons.Default.KeyboardArrowDown,
                contentDescription = null,
                tint = AppTheme.color.textSecondaryColor,
                modifier = Modifier
                    .padding(start = 8.dp)
                    .rotate(if (isOpen) 180f else 0f),
            )
        }
        if (isOpen) Bullets(section.items, Modifier.padding(start = 12.dp, end = 12.dp, bottom = 14.dp))
    }
}

@Composable
private fun Bullets(lines: List<String>, modifier: Modifier = Modifier) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(8.dp)) {
        lines.forEach { line ->
            Row {
                Text(text = "•", fontSize = 13.sp, color = AppTheme.color.primary, modifier = Modifier.width(14.dp))
                Text(text = line, fontSize = 13.sp, lineHeight = 19.sp, color = AppTheme.color.textSecondaryColor)
            }
        }
    }
}

@Preview
@Composable
private fun ItemPagePreview() {
    val page = VOItemPage(
        title = "Black King Bar",
        imageUrl = "",
        cost = 4050,
        boughtFrom = null,
        description = "Мощный посох, наделённый силой гигантов.",
        bonuses = listOf(VOBonus("+10", "к силе"), VOBonus("+24", "к урону")),
        recipe = VORecipe(listOf(VOItemLink("mithril_hammer", "Mithril Hammer", "", 1600), VOItemLink("ogre_axe", "Ogre Axe", "", 1000)), 1450),
        upgrades = emptyList(),
        abilities = listOf(
            VOAbility(
                "Активное", "Avatar", "url", "95", "50", listOf("Применение: без цели"),
                listOf("Применяет на владельца нормальное развеивание."), listOf(VOFact("Длительность", "9/8/7 сек.")), emptyList(), listOf("Заметка"),
            ),
        ),
        properties = emptyList(),
        recommendations = listOf(VORecommendation("Героям с длинным произнесением", listOf(VOHero("Enigma", ""), VOHero("Sand King", "")))),
        sections = listOf(VOSection(VOSectionKind.TIPS, listOf("Совет"))),
    )
    AppTheme {
        ItemPage(page, null, {}, {}, {}, {})
    }
}
