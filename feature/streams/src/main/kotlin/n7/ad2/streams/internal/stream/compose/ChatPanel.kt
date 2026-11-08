package n7.ad2.streams.internal.stream.compose

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import n7.ad2.core.ui.compose.AppTheme
import n7.ad2.feature.streams.R
import n7.ad2.streams.internal.model.ChatLine

private val panelBackground = Color(0xFF141416)

/**
 * The chat as Twitch shows it beside the video: the author in the color they picked and the message, the newest at the bottom.
 * The list follows new messages while it is at the bottom; scrolled up it stays where it is and offers a way back down.
 */
@Composable
internal fun ChatPanel(log: StateFlow<List<ChatLine>>, modifier: Modifier = Modifier) {
    // collected only while the panel is on the screen: with the chat off the connection to Twitch closes
    val lines by log.collectAsStateWithLifecycle()
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    val isReading by remember { derivedStateOf { listState.firstVisibleItemIndex > 1 } }
    // the newest line is the first item of the reversed list, a new one pushes the one being looked at to the second place
    LaunchedEffect(lines.lastOrNull()?.id) {
        if (listState.firstVisibleItemIndex <= 1) listState.scrollToItem(0)
    }
    // the panel sits at the edge of the screen, its text stays clear of a camera cutout there
    Box(modifier = modifier.background(panelBackground).windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.End + WindowInsetsSides.Vertical))) {
        LazyColumn(
            state = listState,
            reverseLayout = true,
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
            modifier = Modifier.fillMaxSize(),
        ) {
            items(lines.asReversed(), key = { it.id }) { line ->
                Text(
                    text = buildAnnotatedString {
                        withStyle(SpanStyle(color = readableColor(line.message.color), fontWeight = FontWeight.Bold)) { append(line.message.author) }
                        append(": ")
                        append(line.message.text)
                    },
                    fontSize = 14.sp,
                    lineHeight = 19.sp,
                    color = Color.White,
                    modifier = Modifier.padding(vertical = 3.dp),
                )
            }
        }
        if (lines.isEmpty()) {
            Text(
                text = stringResource(R.string.streams_chat_waiting),
                style = AppTheme.style.body,
                color = AppTheme.color.textSecondaryColor,
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(16.dp),
            )
        }
        AnimatedVisibility(
            visible = isReading,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 12.dp),
        ) {
            Text(
                text = stringResource(R.string.streams_chat_newest),
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier
                    .clip(CircleShape)
                    .background(AppTheme.color.primary)
                    .clickable(role = Role.Button) { scope.launch { listState.animateScrollToItem(0) } }
                    .padding(horizontal = 14.dp, vertical = 6.dp),
            )
        }
    }
}
