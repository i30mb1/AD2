package n7.ad2.hero.page.internal.pager

import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.lerp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import n7.ad2.core.ui.compose.AppTheme
import n7.ad2.feature.hero.page.ui.R
import n7.ad2.hero.page.internal.info.components.AttributeIcon
import n7.ad2.hero.page.internal.info.components.InfoColors
import n7.ad2.hero.page.internal.info.components.format
import n7.ad2.hero.page.internal.info.components.label
import n7.ad2.heroes.domain.model.AttackType
import n7.ad2.heroes.domain.model.HeroAttribute
import n7.ad2.heroes.domain.model.HeroAttributes
import n7.ad2.heroes.domain.model.HeroDescription
import n7.ad2.heroes.domain.model.HeroRole
import n7.ad2.heroes.domain.model.Role as HeroRoleType

internal val BANNER_HEIGHT = 156.dp
internal val TOOLBAR_HEIGHT = 56.dp
private const val ROLES_IN_BANNER = 2

/**
 * Hero art with the name, the short facts and the attributes. [collapse] 0 is the open banner, 1 is a plain
 * toolbar with a small avatar: the banner folds while a tab's list scrolls, so the page keeps its height for content.
 */
@Composable
internal fun HeroBanner(
    heroName: String,
    artUrl: String,
    description: HeroDescription?,
    collapse: Float,
    trailing: (@Composable () -> Unit)? = null,
) {
    val backDispatcher = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher
    val height: Dp = lerp(BANNER_HEIGHT, TOOLBAR_HEIGHT, collapse)
    // the details fade out in the first half of the fold, the toolbar avatar fades in during the second half
    val detailsAlpha = (1f - collapse * 2f).coerceIn(0f, 1f)
    val toolbarAlpha = (collapse * 2f - 1f).coerceIn(0f, 1f)
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(height)
            .clip(androidx.compose.ui.graphics.RectangleShape),
    ) {
        AsyncImage(
            model = artUrl,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            alignment = Alignment.TopCenter,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .offset(x = 20.dp)
                .width(280.dp)
                .height(BANNER_HEIGHT)
                .alpha(1f - collapse),
        )
        Box(
            Modifier
                .fillMaxSize()
                .background(
                    Brush.horizontalGradient(
                        0.38f to AppTheme.color.background,
                        0.7f to AppTheme.color.background.copy(alpha = 0.55f),
                        1f to AppTheme.color.background.copy(alpha = 0.1f),
                    ),
                ),
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(TOOLBAR_HEIGHT)
                .padding(start = 4.dp, end = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(onClick = { backDispatcher?.onBackPressed() }) {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowLeft,
                    contentDescription = stringResource(R.string.hero_back),
                    tint = AppTheme.color.textColor,
                )
            }
            AsyncImage(
                model = artUrl,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .alpha(toolbarAlpha)
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(AppTheme.color.surface),
            )
            Text(
                text = heroName,
                style = AppTheme.style.H4,
                fontWeight = FontWeight.Bold,
                color = AppTheme.color.textColor,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .weight(1f)
                    .alpha(toolbarAlpha)
                    .padding(start = 10.dp),
            )
            trailing?.invoke()
        }
        Column(
            modifier = Modifier
                .padding(start = 16.dp, top = 52.dp)
                .width(240.dp)
                .graphicsLayer { alpha = detailsAlpha },
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (description != null) {
                    AttributeIcon(description.primaryAttribute, Modifier.size(18.dp))
                    Box(Modifier.width(6.dp))
                }
                Text(
                    text = heroName,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = AppTheme.color.textColor,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            if (description != null) {
                Text(
                    text = facts(description),
                    style = AppTheme.style.body,
                    color = AppTheme.color.textSecondaryColor,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Attributes(description.attributes, description.primaryAttribute)
            }
        }
    }
}

/** "Melee · Complexity 1/3 · Carry, Escape": the strongest roles only, the rest is in the "Info" tab. */
@Composable
private fun facts(description: HeroDescription): String {
    val roles = description.roles
        .sortedByDescending { it.level }
        .take(ROLES_IN_BANNER)
        .map { stringResource(it.role.label) }
    return buildList {
        add(stringResource(description.attackType.label))
        add("${stringResource(R.string.complexity)} ${description.complexity}/3")
        if (roles.isNotEmpty()) add(roles.joinToString(", "))
    }.joinToString(" · ")
}

@Composable
private fun Attributes(attributes: HeroAttributes, primary: HeroAttribute) {
    val isUniversal = primary == HeroAttribute.UNIVERSAL
    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        Attribute(R.drawable.strength, attributes.strength, attributes.strengthGain, InfoColors.strength, isUniversal || primary == HeroAttribute.STRENGTH)
        Attribute(R.drawable.agility, attributes.agility, attributes.agilityGain, InfoColors.agility, isUniversal || primary == HeroAttribute.AGILITY)
        Attribute(R.drawable.intelligence, attributes.intelligence, attributes.intelligenceGain, InfoColors.intelligence, isUniversal || primary == HeroAttribute.INTELLIGENCE)
    }
}

/** The primary attribute is painted in its color, the others stay white. */
@Composable
private fun Attribute(icon: Int, base: Double, gain: Double, color: Color, isPrimary: Boolean) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Image(painterResource(icon), contentDescription = null, modifier = Modifier.size(14.dp))
        Text(
            text = base.format(),
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = if (isPrimary) color else AppTheme.color.textColor,
            modifier = Modifier.padding(start = 4.dp),
        )
        Text(text = "+${gain.format()}", fontSize = 13.sp, color = color, modifier = Modifier.padding(start = 3.dp))
    }
}

/** Tabs in the style of the main menu: the selected one is white with a line under it. */
@Composable
internal fun HeroPageTabs(titles: List<String>, selected: Int, onSelect: (index: Int) -> Unit) {
    Box(modifier = Modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .height(1.dp)
                .background(AppTheme.color.surface),
        )
        Row(modifier = Modifier.fillMaxWidth()) {
            titles.forEachIndexed { index, title ->
                val isSelected = index == selected
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp)
                        .clickable(role = Role.Tab) { onSelect(index) },
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = title.uppercase(),
                        style = AppTheme.style.H5,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        color = if (isSelected) AppTheme.color.textColor else AppTheme.color.textSecondaryColor.copy(alpha = 0.7f),
                    )
                    if (isSelected) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .padding(horizontal = 24.dp)
                                .fillMaxWidth()
                                .height(2.dp)
                                .background(AppTheme.color.primary),
                        )
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun HeroBannerPreview() {
    val description = HeroDescription(
        name = "Juggernaut",
        primaryAttribute = HeroAttribute.AGILITY,
        attackType = AttackType.MELEE,
        complexity = 1,
        roles = listOf(HeroRole(HeroRoleType.CARRY, 2), HeroRole(HeroRoleType.ESCAPE, 1), HeroRole(HeroRoleType.PUSHER, 1)),
        attributes = HeroAttributes(20.0, 2.0, 32.0, 2.8, 14.0, 1.4),
    )
    AppTheme(isDarkTheme = true) {
        Column(Modifier.background(AppTheme.color.background)) {
            HeroBanner("Juggernaut", "", description, collapse = 0f)
            HeroPageTabs(listOf("Инфа", "Реплики", "Гайды"), selected = 0, onSelect = {})
            Box(Modifier.height(16.dp))
            HeroBanner("Juggernaut", "", description, collapse = 1f)
        }
    }
}
