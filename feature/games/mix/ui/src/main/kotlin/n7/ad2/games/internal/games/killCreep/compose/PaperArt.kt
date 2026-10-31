package n7.ad2.games.internal.games.killCreep.compose

import androidx.compose.ui.graphics.Color
import n7.ad2.games.internal.games.killCreep.CreepType

/** Прямоугольник, в который помещается деталь (в координатах её рамки) */
internal data class PartBounds(val left: Float, val top: Float, val right: Float, val bottom: Float)

/**
 * Деталь, вырезанная из бумаги. Координаты - в рамке фигуры (крип 100x100, поле [PaperLane.WIDTH]x[PaperLane.HEIGHT]).
 * [stroke] > 0 - деталь рисуется линией этой толщины, а не заливкой.
 */
internal sealed interface PaperPart {
    val color: Color
    val stroke: Float
    fun bounds(): PartBounds
}

/** Контур из абсолютных команд M, L, Q, Z (как в SVG) */
internal data class PaperPath(val d: String, override val color: Color, override val stroke: Float = 0f) : PaperPart {

    /** Команды контура: буква и её точки (x, y парами) */
    val commands: List<Pair<Char, List<Float>>> = COMMAND.findAll(d).map { match ->
        match.value[0] to NUMBER.findAll(match.value).map { it.value.toFloat() }.toList()
    }.toList()

    override fun bounds(): PartBounds {
        val xs = commands.flatMap { (_, n) -> n.filterIndexed { i, _ -> i % 2 == 0 } }
        val ys = commands.flatMap { (_, n) -> n.filterIndexed { i, _ -> i % 2 == 1 } }
        return PartBounds(xs.min(), ys.min(), xs.max(), ys.max())
    }

    private companion object {
        val COMMAND = Regex("[MLQZ][^MLQZ]*")
        val NUMBER = Regex("-?\\d+(\\.\\d+)?")
    }
}

internal data class PaperCircle(val cx: Float, val cy: Float, val r: Float, override val color: Color, override val stroke: Float = 0f) : PaperPart {
    override fun bounds() = PartBounds(cx - r, cy - r, cx + r, cy + r)
}

internal data class PaperOval(val cx: Float, val cy: Float, val rx: Float, val ry: Float, override val color: Color) : PaperPart {
    override val stroke: Float get() = 0f
    override fun bounds() = PartBounds(cx - rx, cy - ry, cx + rx, cy + ry)
}

internal data class PaperRect(val x: Float, val y: Float, val w: Float, val h: Float, val radius: Float, override val color: Color) : PaperPart {
    override val stroke: Float get() = 0f
    override fun bounds() = PartBounds(x, y, x + w, y + h)
}

/** Крип в два слоя: [body] - силуэт под бумажной кромкой, [details] - наклеено сверху с маленькой тенью */
internal data class PaperFigure(val body: List<PaperPart>, val details: List<PaperPart>)

/** Как крип лежит на поле: кромка, высота над полем (тень) и наклон. Добиваемый приподнят и с золотой кромкой */
internal data class PaperLook(val edge: Color, val edgeWidth: Float, val lift: Float, val tilt: Float) {
    companion object {
        fun of(killable: Boolean): PaperLook = if (killable) {
            PaperLook(edge = PaperColors.Gold, edgeWidth = 9f, lift = 11f, tilt = -4f)
        } else {
            PaperLook(edge = PaperColors.Edge, edgeWidth = 8f, lift = 5f, tilt = 0f)
        }
    }
}

internal object PaperColors {
    val Edge = Color(0xFFF2EADB)
    val Gold = Color(0xFFFFD166)
    val Ink = Color(0xFF1A1514)
}

/** Палитра Dire: игрок добивает вражеских крипов. У мега-крипа золотой доспех */
private class Palette(
    val armor: Color = Color(0xFF7A2E26),
    val dark: Color = Color(0xFF3E1A17),
    val shield: Color = Color(0xFF4A4E54),
    val rim: Color = Color(0xFFB0352A),
    val emblem: Color = Color(0xFFE9D8A6),
) {
    val eye = Color(0xFFFF6A3D)
    val bone = Color(0xFFE9D8A6)
    val metal = Color(0xFF9AA0A6)
    val wood = Color(0xFF5A3A22)
    val banner = Color(0xFF8E2B23)
    val orb = Color(0xFFC77DFF)
    val robe = Color(0xFF6B2C3A)
}

/** Бумажные крипы Dire: ближний, дальний, катапульта, знаменосец, мега */
internal object PaperCreeps {

    private val dire = Palette()
    private val mega = Palette(armor = Color(0xFFD9A441), dark = Color(0xFF7A5A1C), shield = Color(0xFFB8862B), emblem = Color(0xFFB0352A))

    private val figures: Map<CreepType, PaperFigure> = mapOf(
        CreepType.MELEE to melee(dire, crown = false),
        CreepType.RANGED to ranged(dire),
        CreepType.SIEGE to siege(dire),
        CreepType.FLAG to flag(dire),
        CreepType.MEGA to melee(mega, crown = true),
    )

    fun figure(type: CreepType): PaperFigure = figures.getValue(type)

    private fun melee(p: Palette, crown: Boolean) = PaperFigure(
        body = listOfNotNull(
            PaperPath("M72 14 L79 16 L75 64 L69 64 Z", p.metal),
            PaperRect(40f, 72f, 9f, 17f, 2f, p.dark),
            PaperRect(54f, 72f, 9f, 17f, 2f, p.dark),
            PaperPath("M31 78 Q29 52 40 44 L62 44 Q73 52 71 78 Z", p.armor),
            PaperPath("M38 46 Q37 24 51 21 Q65 24 64 46 Z", p.armor),
            PaperPath("M41 32 Q24 26 26 10 Q32 22 45 27 Z", p.bone),
            PaperPath("M61 32 Q78 26 76 10 Q70 22 57 27 Z", p.bone),
            if (crown) PaperPath("M38 22 L42 10 L47 18 L51 6 L55 18 L60 10 L64 22 Z", PaperColors.Gold) else null,
            PaperCircle(29f, 62f, 17f, p.shield),
        ),
        details = listOf(
            PaperRect(42f, 32f, 18f, 5f, 2f, PaperColors.Ink),
            PaperCircle(47f, 34.5f, 1.6f, p.eye),
            PaperCircle(55f, 34.5f, 1.6f, p.eye),
            PaperRect(34f, 62f, 34f, 5f, 1.5f, p.wood),
            PaperCircle(29f, 62f, 12f, p.rim, stroke = 3f),
            PaperCircle(29f, 62f, 5f, p.emblem),
            PaperRect(66f, 61f, 15f, 4f, 1.5f, p.wood),
            PaperPath("M33 46 L28 38 L40 44 Z", p.bone),
            PaperPath("M69 46 L74 38 L62 44 Z", p.bone),
        ),
    )

    private fun ranged(p: Palette) = PaperFigure(
        body = listOf(
            PaperRect(73f, 22f, 5f, 68f, 2f, p.wood),
            PaperCircle(75.5f, 17f, 10f, p.orb),
            PaperPath("M29 90 Q27 56 39 42 Q50 32 61 42 Q73 56 71 90 Z", p.robe),
            PaperPath("M36 50 Q35 22 50 16 Q65 22 64 50 Z", p.dark),
            PaperPath("M44 18 Q38 6 30 8 Q38 12 40 22 Z", p.bone),
            PaperPath("M56 18 Q62 6 70 8 Q62 12 60 22 Z", p.bone),
        ),
        details = listOf(
            PaperOval(50f, 38f, 9f, 10f, PaperColors.Ink),
            PaperCircle(46.5f, 38f, 1.8f, p.eye),
            PaperCircle(53.5f, 38f, 1.8f, p.eye),
            PaperCircle(75.5f, 17f, 4.5f, Color.White.copy(alpha = 0.75f)),
            PaperPath("M50 52 L50 90", p.dark, stroke = 5f),
            PaperRect(34f, 62f, 32f, 5f, 1.5f, p.emblem),
            PaperPath("M66 60 L74 54 L76 60 L70 64 Z", p.robe),
        ),
    )

    private fun siege(p: Palette) = PaperFigure(
        body = listOf(
            PaperPath("M42 54 L72 14 L80 20 L52 58 Z", p.wood),
            PaperCircle(78f, 15f, 10f, p.dark),
            PaperRect(12f, 48f, 72f, 24f, 4f, p.armor),
            PaperPath("M14 48 L19 36 L24 48 Z", p.bone),
            PaperPath("M32 48 L37 36 L42 48 Z", p.bone),
            PaperPath("M60 48 L65 36 L70 48 Z", p.bone),
            PaperCircle(29f, 76f, 12f, p.dark),
            PaperCircle(67f, 76f, 12f, p.dark),
        ),
        details = listOf(
            PaperCircle(78f, 12f, 6f, p.metal),
            PaperRect(12f, 58f, 72f, 3f, 0f, p.wood),
            PaperRect(46f, 48f, 4f, 24f, 0f, p.wood),
            PaperCircle(29f, 76f, 4.5f, p.metal),
            PaperCircle(67f, 76f, 4.5f, p.metal),
            PaperCircle(65f, 62f, 4f, p.emblem),
        ),
    )

    private fun flag(p: Palette) = PaperFigure(
        body = listOf(
            PaperPath("M34 10 L82 14 L70 27 L82 41 L34 43 Z", p.banner),
            PaperRect(28f, 4f, 7f, 86f, 3f, p.wood),
            PaperRect(46f, 76f, 8f, 14f, 2f, p.dark),
            PaperRect(58f, 76f, 8f, 14f, 2f, p.dark),
            PaperPath("M38 82 Q36 60 46 54 L66 54 Q76 60 74 82 Z", p.armor),
            PaperPath("M45 56 Q44 40 56 38 Q68 40 67 56 Z", p.armor),
            PaperPath("M47 46 Q36 42 37 32 Q41 40 50 43 Z", p.bone),
            PaperPath("M65 46 Q76 42 75 32 Q71 40 62 43 Z", p.bone),
        ),
        details = listOf(
            PaperCircle(56f, 27f, 7f, p.emblem),
            PaperRect(49f, 45f, 14f, 4f, 2f, PaperColors.Ink),
            PaperCircle(53f, 47f, 1.4f, p.eye),
            PaperCircle(59f, 47f, 1.4f, p.eye),
            PaperRect(34f, 62f, 10f, 5f, 2f, p.armor),
            PaperRect(40f, 68f, 34f, 4f, 1.5f, p.wood),
        ),
    )
}

/** Группа деталей поля: [edge] > 0 - у группы бумажная кромка этой толщины и тень */
internal data class PaperGroup(val parts: List<PaperPart>, val edge: Float = 0f)

/** Фон поля: трава, река поперёк, песчаная линия по диагонали, камни и деревья по краям */
internal object PaperLane {
    const val WIDTH = 360f
    const val HEIGHT = 680f

    val Grass = Color(0xFF35532F)
    private val patch = Color(0xFF2E4A2C)
    private val sand = Color(0xFFA8936A)
    private val rock = Color(0xFF9C8760)
    private val water = Color(0xFF3A6A86)
    private val ripple = Color(0xFF7FB2CC)

    val groups: List<PaperGroup> = listOf(
        PaperGroup(
            listOf(
                PaperPath("M-10 120 Q60 90 110 140 Q90 210 20 220 Q-20 200 -10 120 Z", patch),
                PaperPath("M260 470 Q330 440 380 480 L380 600 Q320 620 270 580 Q240 520 260 470 Z", patch),
            ),
        ),
        PaperGroup(
            listOf(PaperPath("M-20 300 Q60 280 120 330 Q180 380 240 400 Q300 420 380 400 L380 460 Q300 480 230 460 Q160 440 110 395 Q60 350 -20 360 Z", water)),
            edge = 5f,
        ),
        PaperGroup(
            listOf(
                PaperPath("M30 322 Q70 318 100 340", ripple, stroke = 3f),
                PaperPath("M200 410 Q240 425 280 428", ripple, stroke = 3f),
            ),
        ),
        PaperGroup(listOf(PaperPath("M-40 640 L-40 520 L300 -40 L420 -40 L420 60 L60 720 Z", sand)), edge = 6f),
        PaperGroup(
            listOf(
                PaperOval(83f, 477f, 13f, 7f, rock),
                PaperOval(160f, 305f, 10f, 5.5f, rock),
                PaperOval(262f, 176f, 12f, 6f, rock),
                PaperOval(199f, 565f, 9f, 5f, rock),
            ),
        ),
    ) + listOf(
        Triple(36f, 52f, 1f), Triple(92f, 34f, 0.8f), Triple(26f, 236f, 0.75f), Triple(330f, 250f, 0.9f),
        Triple(320f, 560f, 1f), Triple(268f, 640f, 0.8f), Triple(340f, 650f, 0.7f),
    ).flatMap { (x, y, s) -> tree(x, y, s) }

    val parts: List<PaperPart> get() = groups.flatMap { it.parts }

    /** Крона из трёх кругов с кромкой и светлая верхушка поверх */
    private fun tree(x: Float, y: Float, s: Float): List<PaperGroup> {
        val crown = Color(0xFF2C4529)
        return listOf(
            PaperGroup(
                listOf(
                    PaperCircle(x, y - 10 * s, 20 * s, crown),
                    PaperCircle(x - 12 * s, y + 2 * s, 14 * s, crown),
                    PaperCircle(x + 12 * s, y + 2 * s, 14 * s, crown),
                ),
                edge = 5f,
            ),
            PaperGroup(
                listOf(
                    PaperCircle(x - 4 * s, y - 14 * s, 11 * s, Color(0xFF4C7442)),
                    PaperCircle(x + 8 * s, y - 4 * s, 7 * s, Color(0xFF3E6237)),
                ),
            ),
        )
    }
}
