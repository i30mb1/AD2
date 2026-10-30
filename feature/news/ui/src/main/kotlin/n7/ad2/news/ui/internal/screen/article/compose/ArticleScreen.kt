package n7.ad2.news.ui.internal.screen.article.compose

import android.content.Intent
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.windowInsetsTopHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Share
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withLink
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import n7.ad2.core.ui.compose.AppTheme
import n7.ad2.core.ui.compose.view.LoadingAnimation
import n7.ad2.feature.news.ui.R
import n7.ad2.items.domain.model.ArticleBlock
import n7.ad2.items.domain.model.ArticleText
import n7.ad2.news.ui.internal.screen.article.ArticleViewModel

private const val IMAGE_RATIO = 16f / 9f
private val bodyColor = Color(0xFFE3E5E8)
private val linkColor = Color(0xFFFF6E62)
private val pressBadgeColor = Color(0xFF3A5A80)

/** Consecutive list items are drawn as one card, so the screen works with these instead of raw blocks. */
private sealed interface ArticlePart {
    data class Single(val block: ArticleBlock) : ArticlePart
    data class BulletList(val items: List<ArticleBlock.ListItem>) : ArticlePart
}

@Composable
internal fun ArticleScreen(viewModel: ArticleViewModel) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val header = state.header
    val bottomInset = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
    // the feed already shows the first picture of the post on top, it isn't repeated in the text
    val parts = state.blocks
        .filterIndexed { index, block -> !(index == 0 && block is ArticleBlock.Image && block.url == header?.imageUrl) }
        .toParts()

    val listState = rememberLazyListState()
    // once the picture is scrolled away the text would run under the clock, a scrim keeps the status bar readable
    val isHeaderScrolledAway by remember { derivedStateOf { listState.firstVisibleItemIndex > 0 } }

    Box(Modifier.fillMaxSize()) {
        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = bottomInset + 24.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            item(key = "header") { HeaderImage(header) }
            if (header != null) {
                item(key = "meta") { Meta(header, Modifier.padding(horizontal = 16.dp)) }
                item(key = "title") {
                    Text(
                        text = header.title,
                        fontSize = 26.sp,
                        lineHeight = 31.sp,
                        fontWeight = FontWeight.Bold,
                        color = AppTheme.color.textColor,
                        modifier = Modifier.padding(horizontal = 16.dp),
                    )
                }
            }
            when {
                state.isLoading -> item(key = "loading") {
                    Box(Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) { LoadingAnimation() }
                }
                state.isError -> item(key = "error") { ErrorBlock(onRetry = viewModel::loadBody) }
                else -> parts.forEachIndexed { index, part ->
                    item(key = "part-$index") { ArticlePartView(part, Modifier.padding(horizontal = 16.dp)) }
                }
            }
            if (header != null && header.url.isNotEmpty() && !state.isLoading) {
                item(key = "original") { OpenOriginalButton(header.url, Modifier.padding(horizontal = 16.dp)) }
            }
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
private fun HeaderImage(header: ArticleViewModel.Header?) {
    val backDispatcher = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher
    val context = LocalContext.current
    val shareTitle = stringResource(R.string.news_share)
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(IMAGE_RATIO)
            .background(AppTheme.color.surface),
    ) {
        if (header != null) {
            AsyncImage(model = header.imageUrl, contentDescription = null, contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize())
        }
        Box(
            Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        0f to AppTheme.color.background.copy(alpha = 0.55f),
                        0.3f to Color.Transparent,
                        0.7f to Color.Transparent,
                        1f to AppTheme.color.background,
                    ),
                ),
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 12.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            RoundButton(Icons.Default.KeyboardArrowLeft, stringResource(R.string.news_back)) { backDispatcher?.onBackPressed() }
            if (header != null && header.url.isNotEmpty()) {
                RoundButton(Icons.Default.Share, shareTitle) {
                    val send = Intent(Intent.ACTION_SEND)
                        .setType("text/plain")
                        .putExtra(Intent.EXTRA_SUBJECT, header.title)
                        .putExtra(Intent.EXTRA_TEXT, "${header.title}\n${header.url}")
                    context.startActivity(Intent.createChooser(send, shareTitle))
                }
            }
        }
    }
}

@Composable
private fun RoundButton(icon: ImageVector, description: String, onClick: () -> Unit) {
    IconButton(
        onClick = onClick,
        modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(Color.Black.copy(alpha = 0.5f)),
    ) {
        Icon(imageVector = icon, contentDescription = description, tint = Color.White)
    }
}

@Composable
private fun Meta(header: ArticleViewModel.Header, modifier: Modifier) {
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        if (header.source.isNotEmpty()) {
            Text(
                text = header.source.uppercase(),
                style = AppTheme.style.body,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier
                    .clip(AppTheme.shape.small)
                    .background(if (header.isOfficial) AppTheme.color.primary else pressBadgeColor)
                    .padding(horizontal = 6.dp, vertical = 2.dp),
            )
        }
        val details = listOf(header.date, header.author).filter { it.isNotEmpty() }.joinToString(" · ")
        if (details.isNotEmpty()) {
            Text(
                text = details,
                style = AppTheme.style.body,
                color = AppTheme.color.textSecondaryColor,
                maxLines = 1,
                modifier = Modifier.padding(start = 8.dp),
            )
        }
    }
}

@Composable
private fun ArticlePartView(part: ArticlePart, modifier: Modifier) {
    when (part) {
        is ArticlePart.BulletList -> Column(
            modifier = modifier
                .fillMaxWidth()
                .clip(AppTheme.shape.medium)
                .background(AppTheme.color.surface)
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            part.items.forEach { item ->
                Row {
                    Box(
                        Modifier
                            .padding(top = 7.dp)
                            .size(6.dp)
                            .clip(AppTheme.shape.small)
                            .background(AppTheme.color.primary),
                    )
                    Text(
                        text = item.text.toAnnotatedString(),
                        fontSize = 14.sp,
                        lineHeight = 20.sp,
                        color = bodyColor,
                        modifier = Modifier.padding(start = 10.dp),
                    )
                }
            }
        }
        is ArticlePart.Single -> when (val block = part.block) {
            is ArticleBlock.Heading -> Text(
                text = block.text.toAnnotatedString(),
                fontSize = 19.sp,
                lineHeight = 24.sp,
                fontWeight = FontWeight.Bold,
                color = AppTheme.color.textColor,
                modifier = modifier.padding(top = 6.dp),
            )
            is ArticleBlock.Paragraph -> Text(
                text = block.text.toAnnotatedString(),
                fontSize = 15.sp,
                lineHeight = 23.sp,
                color = bodyColor,
                modifier = modifier,
            )
            is ArticleBlock.Quote -> Text(
                text = block.text.toAnnotatedString(),
                fontSize = 15.sp,
                lineHeight = 23.sp,
                color = bodyColor,
                modifier = modifier
                    .fillMaxWidth()
                    .clip(AppTheme.shape.medium)
                    .background(AppTheme.color.surface)
                    .padding(horizontal = 14.dp, vertical = 12.dp),
            )
            is ArticleBlock.Image -> AsyncImage(
                model = block.url,
                contentDescription = null,
                contentScale = ContentScale.FillWidth,
                modifier = modifier
                    .fillMaxWidth()
                    .clip(AppTheme.shape.medium),
            )
            is ArticleBlock.ListItem -> Unit
        }
    }
}

@Composable
private fun OpenOriginalButton(url: String, modifier: Modifier) {
    val uriHandler = LocalUriHandler.current
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp)
            .clip(AppTheme.shape.medium)
            .border(1.dp, AppTheme.color.primary, AppTheme.shape.medium)
            .clickable(role = Role.Button) { uriHandler.openUri(url) },
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = stringResource(R.string.news_open_original).uppercase(),
            style = AppTheme.style.H5,
            fontWeight = FontWeight.Bold,
            color = AppTheme.color.textColor,
        )
        Icon(
            imageVector = Icons.Default.KeyboardArrowRight,
            contentDescription = null,
            tint = AppTheme.color.primary,
            modifier = Modifier
                .padding(start = 4.dp)
                .size(20.dp),
        )
    }
}

@Composable
private fun ErrorBlock(onRetry: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(text = stringResource(R.string.news_article_error), style = AppTheme.style.H5, color = AppTheme.color.textSecondaryColor)
        Text(
            text = stringResource(R.string.news_retry).uppercase(),
            style = AppTheme.style.H5,
            fontWeight = FontWeight.Bold,
            color = AppTheme.color.primary,
            modifier = Modifier
                .clip(AppTheme.shape.medium)
                .clickable(role = Role.Button, onClick = onRetry)
                .padding(horizontal = 16.dp, vertical = 10.dp),
        )
    }
}

private fun List<ArticleBlock>.toParts(): List<ArticlePart> {
    val result = mutableListOf<ArticlePart>()
    val list = mutableListOf<ArticleBlock.ListItem>()
    fun flushList() {
        if (list.isNotEmpty()) result.add(ArticlePart.BulletList(list.toList()))
        list.clear()
    }
    forEach { block ->
        if (block is ArticleBlock.ListItem) {
            list.add(block)
        } else {
            flushList()
            result.add(ArticlePart.Single(block))
        }
    }
    flushList()
    return result
}

private fun List<ArticleText>.toAnnotatedString(): AnnotatedString = buildAnnotatedString {
    forEach { part ->
        val style = SpanStyle(
            fontWeight = if (part.isBold) FontWeight.Bold else null,
            fontStyle = if (part.isItalic) FontStyle.Italic else null,
        )
        val link = part.link
        if (link != null) {
            withLink(LinkAnnotation.Url(link, TextLinkStyles(style.copy(color = linkColor, textDecoration = TextDecoration.Underline)))) { append(part.text) }
        } else {
            withStyle(style) { append(part.text) }
        }
    }
}
