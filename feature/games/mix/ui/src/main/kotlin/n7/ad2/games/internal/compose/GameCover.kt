package n7.ad2.games.internal.compose

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import n7.ad2.feature.games.mix.ui.R
import n7.ad2.games.internal.data.GameVO

private const val ASSETS = "file:///android_asset/"
private val Gold = Color(0xFFFFD166)

/**
 * Обложка игры из картинок Доты, которые уже лежат в ассетах приложения: каждая показывает, о чем игра.
 * Детали (значки, «VS», «+») масштабируются от [size], обложка читается и на 48dp, и крупно.
 */
@Composable
internal fun GameCover(game: GameVO, size: Dp, modifier: Modifier = Modifier, shape: Shape = RoundedCornerShape(size * 0.18f)) {
    Box(
        modifier = modifier
            .size(size)
            .clip(shape)
            .background(Color(0xFF111215)),
    ) {
        when (game) {
            is GameVO.GuessSkillMana, is GameVO.CanYouBuyIt -> SpellCover(size)
            is GameVO.GuessHero -> HeroCover(size)
            is GameVO.HigherLower -> CompareCover(size)
            is GameVO.BuildItem -> BuildCover(size)
            is GameVO.Apm -> CreepCover(size)
            is GameVO.XO -> XoCover(size)
        }
    }
}

/** Способность и значок маны с вопросом: сколько стоит */
@Composable
private fun BoxScope.SpellCover(size: Dp) {
    Asset("images/black_hole.webp", Modifier.fillMaxSize())
    Badge(
        text = "?",
        modifier = Modifier
            .align(Alignment.BottomEnd)
            .padding(size * 0.06f)
            .size(size * 0.32f)
            .background(Color(0xFF1E5BD8), CircleShape)
            .border(size * 0.025f, Color(0xFF9CC4FF), CircleShape),
        size = size * 0.18f,
    )
}

/** Размытый портрет: кто это */
@Composable
private fun BoxScope.HeroCover(size: Dp) {
    val darken = ColorFilter.colorMatrix(ColorMatrix().apply { setToScale(0.55f, 0.55f, 0.6f, 1f) })
    // blur работает с Android 12, раньше портрет просто затемнен
    Asset("heroes/invoker/full.webp", Modifier.fillMaxSize().scale(1.3f).blur(size * 0.05f), colorFilter = darken)
    Box(Modifier.fillMaxSize().background(Brush.radialGradient(listOf(Color.Transparent, Color(0xCC0D1420)))))
    Badge("?", Modifier.align(Alignment.Center), size = size * 0.42f, color = Color(0xFFCFE9FF))
}

/** Два героя по диагонали и «VS»: у кого больше */
@Composable
private fun BoxScope.CompareCover(size: Dp) {
    val left = GenericShape { box, _ -> moveTo(0f, 0f); lineTo(box.width * 0.6f, 0f); lineTo(box.width * 0.4f, box.height); lineTo(0f, box.height); close() }
    val right = GenericShape { box, _ -> moveTo(box.width * 0.6f, 0f); lineTo(box.width, 0f); lineTo(box.width, box.height); lineTo(box.width * 0.4f, box.height); close() }
    Asset("heroes/juggernaut/full.webp", Modifier.fillMaxSize().clip(left), alignment = Alignment.CenterStart)
    Asset("heroes/pudge/full.webp", Modifier.fillMaxSize().clip(right), alignment = Alignment.CenterEnd)
    Badge(
        text = "VS",
        modifier = Modifier
            .align(Alignment.Center)
            .size(width = size * 0.36f, height = size * 0.24f)
            .background(Color(0xCC000000), RoundedCornerShape(size * 0.08f))
            .border(1.dp, Gold, RoundedCornerShape(size * 0.08f)),
        size = size * 0.13f,
        color = Gold,
    )
}

/** Предмет и его компоненты: собери */
@Composable
private fun BuildCover(size: Dp) {
    Box(Modifier.fillMaxSize().background(Brush.radialGradient(listOf(Color(0xFF4A3A1A), Color(0xFF15120B)))))
    Column(Modifier.fillMaxSize().padding(size * 0.07f), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.SpaceBetween) {
        Asset("items/black_king_bar/full.webp", Modifier.fillMaxWidth(0.7f).aspectRatio(88f / 64f).clip(RoundedCornerShape(size * 0.06f)))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Asset("items/ogre_axe/full.webp", Modifier.fillMaxWidth(0.36f).aspectRatio(88f / 64f).clip(RoundedCornerShape(size * 0.04f)))
            Badge("+", Modifier, size = size * 0.16f, color = Gold)
            Asset("items/mithril_hammer/full.webp", Modifier.fillMaxWidth(0.56f).aspectRatio(88f / 64f).clip(RoundedCornerShape(size * 0.04f)))
        }
    }
}

/** Крип на дорожке и золото за добивание */
@Composable
private fun BoxScope.CreepCover(size: Dp) {
    Image(
        painter = painterResource(R.drawable.background_apm),
        contentDescription = null,
        contentScale = ContentScale.Crop,
        alignment = Alignment.BottomStart,
        colorFilter = ColorFilter.colorMatrix(ColorMatrix().apply { setToScale(0.6f, 0.6f, 0.6f, 1f) }),
        modifier = Modifier.fillMaxSize(),
    )
    // пиксельный спрайт: без сглаживания при увеличении
    Image(
        painter = BitmapPainter(ImageBitmap.imageResource(R.drawable.cover_creep), filterQuality = FilterQuality.None),
        contentDescription = null,
        modifier = Modifier.align(Alignment.Center).size(size * 0.64f).offset(y = size * 0.04f),
    )
    Badge(
        text = "+38",
        modifier = Modifier
            .align(Alignment.TopEnd)
            .padding(size * 0.05f)
            .background(Color(0xBB000000), RoundedCornerShape(size * 0.06f))
            .padding(horizontal = size * 0.05f),
        size = size * 0.13f,
        color = Gold,
    )
}

/** Крестики-нолики на поле Света и Тьмы */
@Composable
private fun XoCover(size: Dp) {
    val radiant = Color(0xFF1F4D2B)
    val dire = Color(0xFF4D1F1F)
    Box(
        Modifier
            .fillMaxSize()
            .drawWithContent {
                drawRect(radiant)
                val path = androidx.compose.ui.graphics.Path().apply { moveTo(this@drawWithContent.size.width, 0f); lineTo(this@drawWithContent.size.width, this@drawWithContent.size.height); lineTo(0f, this@drawWithContent.size.height); close() }
                drawPath(path, dire)
            },
    )
    val cells = listOf("X", "O", "", "", "X", "O", "O", "", "X")
    Column(Modifier.fillMaxSize().padding(size * 0.12f), verticalArrangement = Arrangement.spacedBy(size * 0.03f)) {
        cells.chunked(3).forEach { row ->
            Row(Modifier.weight(1f).fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(size * 0.03f)) {
                row.forEach { cell ->
                    Box(Modifier.weight(1f).fillMaxSize().background(Color(0x66000000), RoundedCornerShape(size * 0.04f)), contentAlignment = Alignment.Center) {
                        if (cell.isNotEmpty()) Badge(cell, Modifier, size = size * 0.14f, color = if (cell == "X") Color(0xFF7FE08A) else Color(0xFFFF8A80))
                    }
                }
            }
        }
    }
}

@Composable
private fun Asset(path: String, modifier: Modifier, alignment: Alignment = Alignment.Center, colorFilter: ColorFilter? = null) {
    AsyncImage(
        model = ASSETS + path,
        contentDescription = null,
        contentScale = ContentScale.Crop,
        alignment = alignment,
        colorFilter = colorFilter,
        modifier = modifier,
    )
}

@Composable
private fun Badge(text: String, modifier: Modifier, size: Dp, color: Color = Color.White) {
    Box(modifier, contentAlignment = Alignment.Center) {
        Text(text, color = color, fontSize = (size.value).sp, fontWeight = FontWeight.ExtraBold, textAlign = TextAlign.Center)
    }
}

@Preview(backgroundColor = 0xFF202225, showBackground = true, widthDp = 420)
@Composable
private fun GameCoverPreview() {
    val context = androidx.compose.ui.platform.LocalContext.current
    val res = object : n7.ad2.Resources {
        override fun getString(resourceID: Int): String = context.getString(resourceID)
        override fun getString(resourceID: Int, formatArgs: Any): String = context.getString(resourceID, formatArgs)
        override fun getAssets(path: String) = throw UnsupportedOperationException()
        override fun getExternalFilesDir(path: String) = null
        override fun getFilesDir() = null
        override fun getConfiguration() = null
    }
    Row(Modifier.padding(8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        listOf(GameVO.GuessSkillMana(res), GameVO.GuessHero(res), GameVO.HigherLower(res), GameVO.BuildItem(res), GameVO.Apm(res), GameVO.XO(res))
            .forEach { GameCover(it, 64.dp) }
    }
}
