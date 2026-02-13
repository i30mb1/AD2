package n7.ad2.parser

internal fun String.removeBrackets(): String = removeSurrounding("(", ")")

internal fun String.toCamelCase(delimiter: String = " "): String = split(delimiter).joinToString(delimiter) { word ->
    word.replaceFirstChar(Char::titlecaseChar)
}
