package n7.ad2.games.internal.compose

import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp

// https://tusharpingale.hashnode.dev/building-your-own-custom-layout-with-jetpack-compose
@Preview(device = Devices.PIXEL)
@Composable
fun TextMessage(
    message: String = "Hellooooooooooooooo",
    time: String = "11:22",
    modifier: Modifier = Modifier,
) {
    val textMessageDimens = remember { TextMessageDimens() }
    val content = @Composable {
        val onTextLayout: (TextLayoutResult) -> Unit = { textLayoutResult: TextLayoutResult ->
            textMessageDimens.apply {
                this.message = message
                this.messageWidth = textLayoutResult.size.width
                this.lineCount = textLayoutResult.lineCount
                this.lastLineWidth = textLayoutResult.getLineRight(textLayoutResult.lineCount - 1)
            }
        }
        Message(
            modifier = Modifier.padding(start = 8.dp, end = 8.dp, top = 8.dp, bottom = 8.dp),
            message = message,
            onTextLayout = onTextLayout
        )
        Time(modifier = Modifier.padding(start = 4.dp, end = 8.dp, bottom = 4.dp), time = time)
    }
    CustomTextMessageLayout(content = content, modifier = modifier, textMessageDimens = textMessageDimens)
}

@Composable
internal fun CustomTextMessageLayout(
    content: @Composable () -> Unit,
    modifier: Modifier,
    textMessageDimens: TextMessageDimens,
) {
    Layout(content = content, modifier = modifier) { measurables: List<Measurable>, constraints: Constraints ->
        val placeables: List<Placeable> = measurables.map { measurable ->
            measurable.measure(Constraints(0, constraints.maxWidth))
        }
        //check if the message and time composable are measured, else throw IllegalArgumentException exception
        require(placeables.size == 2)

        val message = placeables.first()
        val time = placeables.last()

        //Calculation how big the layout should be {Height and Width of Layout}
        textMessageDimens.parentWidth = constraints.maxWidth
        val padding = (message.measuredWidth - textMessageDimens.messageWidth) / 2

        if (textMessageDimens.lineCount > 1 && textMessageDimens.lastLineWidth + time.measuredWidth >= textMessageDimens.messageWidth + padding) {
            textMessageDimens.rowWidth = message.measuredWidth
            textMessageDimens.rowHeight = message.measuredHeight + time.measuredHeight
        } else if (textMessageDimens.lineCount > 1 && textMessageDimens.lastLineWidth + time.measuredWidth < textMessageDimens.messageWidth + padding) {
            textMessageDimens.rowWidth = message.measuredWidth
            textMessageDimens.rowHeight = message.measuredHeight
        } else if (textMessageDimens.lineCount == 1 && message.measuredWidth + time.measuredWidth >= textMessageDimens.parentWidth) {
            textMessageDimens.rowWidth = message.measuredWidth
            textMessageDimens.rowHeight = message.measuredHeight + time.measuredHeight
        } else {
            textMessageDimens.rowWidth = message.measuredWidth + time.measuredWidth
            textMessageDimens.rowHeight = message.measuredHeight
        }
        //Setting max width of the layout
        textMessageDimens.parentWidth = textMessageDimens.rowWidth.coerceAtLeast(minimumValue = constraints.minWidth)

        layout(width = textMessageDimens.parentWidth, height = textMessageDimens.rowHeight) {
            //Place message at (0,0) since we don't need to place it dynamically
            message.placeRelative(0, 0)
            //Place time using (maxAvailableWidth - timeWidth, messageHeight - timeHeight)
            time.placeRelative(
                textMessageDimens.parentWidth - time.width,
                textMessageDimens.rowHeight - time.height
            )
        }
    }
}

@Composable
fun Message(
    modifier: Modifier = Modifier,
    message: String = "ff",
    onTextLayout: (TextLayoutResult) -> Unit = {},
) {
    Text(
        text = message,
        modifier = modifier,
        onTextLayout = onTextLayout,
    )
}

@Composable
fun Time(
    time: String,
    modifier: Modifier = Modifier,
) {
    Text(
        text = time,
        modifier = modifier,
    )
}

class TextMessageDimens {
    var message: String = ""
        internal set
    var messageWidth: Int = 0
        internal set
    var lastLineWidth: Float = 0f
        internal set
    var lineCount: Int = 0
        internal set
    var rowWidth: Int = 0
        internal set
    var rowHeight: Int = 0
        internal set
    var parentWidth: Int = 0
        internal set
}