package n7.ad2.games.internal.compose

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import n7.ad2.feature.games.mix.ui.R
import n7.ad2.ui.compose.AppTheme

@Composable
internal fun Game(title: String, background: Int, onGameClick: () -> Unit, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .height(150.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(AppTheme.color.surface)
            .clickable { onGameClick() },
    ) {
        Image(
            imageVector = ImageVector.vectorResource(id = background),
            contentDescription = title,
            contentScale = ContentScale.Crop,
            alpha = 0.2f,
            modifier = Modifier.fillMaxSize(),
        )
        Text(
            text = title,
            color = AppTheme.color.textColor,
            style = AppTheme.style.H3,
            modifier = Modifier
                .align(Alignment.Center),
        )
    }
}

@Preview
@Composable
private fun GamePreview(@PreviewParameter(PreviewGameTitleProvider::class) title: String) {
    Game(title = title, background = 0, onGameClick = { })
}

private class PreviewGameTitleProvider : PreviewParameterProvider<String> {
    override val values: Sequence<String> = sequenceOf(
        "Spell Cost",
    )
}

private class PreviewGameBackgroundProvider : PreviewParameterProvider<Int> {
    override val values: Sequence<Int> = sequenceOf(
        R.drawable.background_guess_skill,
    )
}
