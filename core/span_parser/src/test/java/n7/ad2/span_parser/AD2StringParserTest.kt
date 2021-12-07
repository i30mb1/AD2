package n7.ad2.span_parser

import com.google.common.truth.Truth
import com.nhaarman.mockitokotlin2.mock
import org.junit.Before
import org.junit.Test

class AD2StringParserTest {

    private val converter = AD2StringParser(mock())
    private val resultAnalyzer = mutableListOf<Analyzer>()
    private val resultText = StringBuilder()

    @Before
    fun before() {
        resultAnalyzer.clear()
        resultText.clear()
    }

    private fun fillFields(text: String) {
        converter.analyzer(text.iterator()) { span: Analyzer ->
            resultAnalyzer.add(span)
            when (span) {
                is EndSpanTag -> resultText.append(span.text)
                is RemainingText -> resultText.append(span.text)
                is StartSpanTag -> resultText.append(span.text)
            }
        }
    }

    @Test
    fun `empty text`() {
        val raw = ""
        fillFields(raw)
        Truth.assertThat(resultText.toString()).isEqualTo("")
        Truth.assertThat(resultAnalyzer).isEmpty()
    }

    @Test
    fun `simple text`() {
        val raw = "если сдашься, тебе конец"
        fillFields(raw)
        Truth.assertThat(resultText.toString()).isEqualTo("если сдашься, тебе конец")
        Truth.assertThat(resultAnalyzer).containsExactly(
            RemainingText("если сдашься, тебе конец")
        )
    }

    @Test
    fun `simple span`() {
        val raw = """
            если стоять на месте, <span color="#FF9C27B0">вперед</span> не продвинишься
        """.trimIndent()
        fillFields(raw)
        Truth.assertThat(resultText.toString()).isEqualTo("если стоять на месте, вперед не продвинишься")
        Truth.assertThat(resultAnalyzer).containsExactly(
            StartSpanTag("если стоять на месте, ", listOf(AttributeAndValue("color", "#FF9C27B0"))),
            EndSpanTag("вперед"),
            RemainingText(" не продвинишься")
        )
    }

    @Test
    fun `simple span with empty attribute`() {
        val raw = """
            если сможешь подняться, то сможешь и <span color="">встать</span>
        """.trimIndent()
        fillFields(raw)
        Truth.assertThat(resultText.toString()).isEqualTo("если сможешь подняться, то сможешь и встать")
        Truth.assertThat(resultAnalyzer).containsExactly(
            StartSpanTag("если сможешь подняться, то сможешь и ", emptyList()),
            EndSpanTag("встать"),
        )
    }

    @Test
    fun `span in span`() {
        val raw = """
            решая<span color="#000000"> проблемы<span color="#FF0000"> одну за одной,</span> вы решите</span> их все
        """.trimIndent()
        fillFields(raw)
        Truth.assertThat(resultText.toString()).isEqualTo("решая проблемы одну за одной, вы решите их все")
        Truth.assertThat(resultAnalyzer).containsExactly(
            StartSpanTag("решая", listOf(AttributeAndValue("color", "#000000"))),
            StartSpanTag(" проблемы", listOf(AttributeAndValue("color", "#FF0000"))),
            EndSpanTag(" одну за одной,"),
            EndSpanTag(" вы решите"),
            RemainingText(" их все")
        )
    }

    @Test
    fun `span in span with new lines`() {
        val raw = """
                никто
                <span style="strike">
                <span color="#FFF44336"> не хочет</span>
                <span style="bold"> чтобы</span>
                 у</span>
                 тебя
                <span color="#FF2196F3"> получилось</span>
        """.trimIndent()
        fillFields(raw)
        Truth.assertThat(resultText.toString()).isEqualTo("никто\n\n не хочет\n чтобы\n у\n тебя\n получилось")
        Truth.assertThat(resultAnalyzer).containsExactly(
            StartSpanTag("никто\n", listOf(AttributeAndValue("style", "strike"))),
            StartSpanTag("\n", listOf(AttributeAndValue("color", "#FFF44336"))),
            EndSpanTag(" не хочет"),
            StartSpanTag("\n", listOf(AttributeAndValue("style", "bold"))),
            EndSpanTag(" чтобы"),
            EndSpanTag("\n у"),
            StartSpanTag("\n тебя\n", listOf(AttributeAndValue("color", "#FF2196F3"))),
            EndSpanTag(" получилось"),
        )
    }

}