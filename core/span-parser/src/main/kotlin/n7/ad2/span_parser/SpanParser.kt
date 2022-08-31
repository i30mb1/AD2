package n7.ad2.span_parser

import android.text.Spannable

interface SpanParser {
    fun toSpannable(string: String, isNightTheme: Boolean = false): Spannable?
}

class SpanParserFake : SpanParser {
    override fun toSpannable(string: String, isNightTheme: Boolean): Spannable? {
        return null
    }
}