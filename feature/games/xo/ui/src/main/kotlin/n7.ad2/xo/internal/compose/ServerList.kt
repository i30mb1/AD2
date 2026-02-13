package n7.ad2.xo.internal.compose

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import n7.ad2.ui.TextWithDotsSuffix
import n7.ad2.ui.compose.AppTheme
import n7.ad2.ui.compose.Bold
import n7.ad2.xo.internal.compose.model.ServerUI

@Preview
@Composable
private fun ServerListPreview(@PreviewParameter(PreviewProvider::class) list: List<ServerUI>) {
    AppTheme {
        ServerList(list, {})
    }
}

@Composable
internal fun ServerList(servers: List<ServerUI>, onServerClicked: (server: ServerUI) -> Unit, modifier: Modifier = Modifier) {
    Column(modifier = modifier.padding(16.dp)) {
        Row(
            modifier = Modifier.padding(8.dp),
        ) {
            TextWithDotsSuffix(
                text = "Searching servers",
                modifier = Modifier.weight(1f),
            )
            Icon(
                imageVector = Icons.Default.Refresh,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(16.dp),
            )
        }
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 30.dp)
                .clip(AppTheme.shape.medium)
                .background(AppTheme.color.surface)
                .animateContentSize(),
        ) {
            items(servers.size) { index ->
                val server = servers[index]
                ServerItem(onServerClicked, server)
                if (servers.size != index) Divider(color = AppTheme.color.background)
            }
        }
    }
}

@Composable
private fun ServerItem(onServerClicked: (server: ServerUI) -> Unit, server: ServerUI) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .heightIn(min = 60.dp)
            .background(if (server.isMe) AppTheme.color.primary else AppTheme.color.surface)
            .clickable { onServerClicked(server) }
            .padding(vertical = 8.dp, horizontal = 16.dp),
    ) {
        Icon(
            imageVector = Icons.Default.Person,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier
                .size(30.dp)
                .clip(AppTheme.shape.small)
                .background(AppTheme.color.background)
                .padding(8.dp),
        )
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = 16.dp),
        ) {
            Text(
                text = "name: ${server.name}",
                style = AppTheme.style.body.Bold,
                color = AppTheme.color.textColor,
            )
            Text(
                text = "ip: ${server.serverIP}",
                style = AppTheme.style.body.Bold,
                color = AppTheme.color.textColor,
            )
            Text(
                text = "port: ${server.port}",
                style = AppTheme.style.body.Bold,
                color = AppTheme.color.textColor,
            )
        }
    }
}

private class PreviewProvider : PreviewParameterProvider<List<ServerUI>> {
    override val values: Sequence<List<ServerUI>> = sequenceOf(
        buildList { repeat(0) { add(ServerUI("Game$it", "192.168.100.0$it", "808$it", true)) } },
        buildList { repeat(1) { add(ServerUI("Game$it", "192.168.100.0$it", "808$it")) } },
        buildList { repeat(2) { add(ServerUI("Game$it", "192.168.100.0$it", "808$it")) } },
        buildList { repeat(3) { add(ServerUI("Game$it", "192.168.100.0$it", "808$it")) } },
    )
}
