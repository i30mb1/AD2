package n7.ad2.ktx

import android.text.Spannable
import android.text.SpannableStringBuilder

private const val SEPARATOR = "- "
private const val COLON = ": "


fun List<CharSequence>.toStringList(withSeparator: Boolean = false): Spannable {
    val builder = SpannableStringBuilder()
    forEachIndexed { index, text ->
        if (withSeparator) builder.append(SEPARATOR)
        builder.append(text)
        if (index != lastIndex) builder.append(System.lineSeparator())
    }
    return builder
}

fun List<String>.toStringListWithDashAfterColon(): String {
    val builder = SpannableStringBuilder()
    forEachIndexed { index, text ->
        builder.append(text)
        if (index != lastIndex) builder.append(System.lineSeparator())
    }
    var startIndex = 0
    var indexOf = builder.indexOf(COLON, startIndex)
    if (indexOf == -1) return builder.toString()

    do {
        builder.insert(indexOf + 1, "\n")
        startIndex++
        indexOf = builder.indexOf(COLON, startIndex)
    } while (indexOf != -1)

    return builder.toString()
}