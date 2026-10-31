package n7.ad2.drawer.internal.adapter

internal object LogFade {

    const val FULLY_VISIBLE_LOGS = 7

    // top - верх строки лога в координатах списка, fadeEnd - откуда (по y) начинаются полностью видимые строки
    fun alpha(top: Int, fadeEnd: Int): Float = if (fadeEnd <= 0) 1f else (top.toFloat() / fadeEnd).coerceIn(0f, 1f)
}
