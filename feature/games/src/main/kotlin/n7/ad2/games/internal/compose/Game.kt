package n7.ad2.games.internal.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import n7.ad2.games.R
import n7.ad2.games.internal.data.GameVO
import n7.ad2.games.internal.data.Players
import n7.ad2.ui.compose.AppTheme

@Preview
@Composable
internal fun Game(
    @PreviewParameter(PreviewGameProvider::class) gameButtonData: GameVO,
    modifier: Modifier = Modifier,
    onGameClicked: (players: Players) -> Unit = { },
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(AppTheme.color.surface),
    ) {
        Text(
            text = gameButtonData.title,
            color = AppTheme.color.textColor,
            style = AppTheme.style.H5,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .align(Alignment.CenterHorizontally),
        )
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround,
        ) {
            Button(
                onClick = { onGameClicked(Players.One) },
            ) {
                Text(
                    text = stringResource(id = R.string.one_player)
                )
            }
            Button(
                onClick = { onGameClicked(Players.Two) },
            ) {
                Text(
                    text = stringResource(id = R.string.two_players)
                )
            }
        }
    }
}

internal class PreviewGameProvider : PreviewParameterProvider<GameVO> {
    override val values: Sequence<GameVO> = sequenceOf(
        GameVO.SpellCost("HOW MANY COST THIS SPELL"),
        GameVO.SpellCost("HOW MANY COST THIS SPELL 2"),
    )
}