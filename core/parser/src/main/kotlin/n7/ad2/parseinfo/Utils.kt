package n7.ad2.parseinfo

internal fun String.removeBrackets(): String {
    return removeSurrounding("(", ")")
}

internal fun String.toCamelCase(delimiter: String = " "): String {
    return split(delimiter).joinToString(delimiter) { word ->
        word.replaceFirstChar(Char::titlecaseChar)
    }
}