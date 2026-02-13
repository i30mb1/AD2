package n7.ad2.spanparser

internal sealed interface Analyzer
internal data class StartSpanTag(val text: String, val attributes: List<AttributeAndValue>) : Analyzer
internal data class EndSpanTag(val text: String) : Analyzer
internal data class RemainingText(val text: String) : Analyzer
