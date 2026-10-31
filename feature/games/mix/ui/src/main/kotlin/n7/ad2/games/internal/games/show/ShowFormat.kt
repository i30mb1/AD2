package n7.ad2.games.internal.games.show

import java.util.Locale

/** 1 350: thousands split by a space whatever the phone's language */
internal fun points(value: Int): String = String.format(Locale.ROOT, "%,d", value).replace(',', ' ')

/** +150, −100 or 0: the points of a round */
internal fun signed(value: Int): String = when {
    value > 0 -> "+${points(value)}"
    value < 0 -> "−${points(-value)}"
    else -> "0"
}
