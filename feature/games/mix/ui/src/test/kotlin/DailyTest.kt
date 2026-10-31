import com.google.common.truth.Truth
import java.time.LocalDate
import n7.ad2.games.internal.games.show.DailyCodec
import n7.ad2.games.internal.games.show.DailyPlan
import n7.ad2.games.internal.games.show.DailyResult
import n7.ad2.games.internal.games.show.DailyStreak
import n7.ad2.games.internal.games.show.RoundAnswer
import n7.ad2.games.internal.games.show.ShowKind
import n7.ad2.games.internal.games.show.dailyShareText
import org.junit.Test

internal class DailyTest {

    private val day = LocalDate.of(2026, 11, 1)

    @Test
    fun `the daily is ten questions, two of each kind, without a final`() {
        val plan = DailyPlan.create()

        Truth.assertThat(plan).hasSize(DailyPlan.QUESTIONS)
        Truth.assertThat(plan.groupingBy { it.kind }.eachCount()).isEqualTo(ShowKind.entries.associateWith { 2 })
        Truth.assertThat(plan.map { it.index }).isEqualTo((0 until 10).toList())
        Truth.assertThat(plan.none { it.final }).isTrue()
        // the same kinds go one after another
        Truth.assertThat(plan.chunked(2).all { (first, second) -> first.kind == second.kind }).isTrue()
    }

    @Test
    fun `everyone gets the same questions on the same day, the next day gives other ones`() {
        Truth.assertThat(DailyPlan.seed(day)).isEqualTo(DailyPlan.seed(LocalDate.of(2026, 11, 1)))
        Truth.assertThat(DailyPlan.seed(day)).isNotEqualTo(DailyPlan.seed(day.plusDays(1)))
        Truth.assertThat(DailyPlan.gameId(day)).isEqualTo("daily-2026-11-01")
    }

    @Test
    fun `the streak grows day by day, a missed day starts it again, the same day changes nothing`() {
        val first = DailyStreak().played(day)
        Truth.assertThat(first).isEqualTo(DailyStreak(day, 1))
        Truth.assertThat(first.played(day)).isEqualTo(first)
        Truth.assertThat(first.played(day.plusDays(1))).isEqualTo(DailyStreak(day.plusDays(1), 2))
        Truth.assertThat(first.played(day.plusDays(3))).isEqualTo(DailyStreak(day.plusDays(3), 1))
    }

    @Test
    fun `the streak still counts the day after the last game and is gone after a missed day`() {
        val streak = DailyStreak(day, 4)

        Truth.assertThat(streak.current(day)).isEqualTo(4)
        Truth.assertThat(streak.current(day.plusDays(1))).isEqualTo(4)
        Truth.assertThat(streak.current(day.plusDays(2))).isEqualTo(0)
        Truth.assertThat(DailyStreak().current(day)).isEqualTo(0)
    }

    @Test
    fun `the result of the day is saved and read back`() {
        val result = DailyResult(day, listOf(RoundAnswer(correct = true, points = 180), RoundAnswer(correct = false, points = 0)))

        Truth.assertThat(DailyCodec.decode(DailyCodec.encode(result))).isEqualTo(result)
        Truth.assertThat(result.correct).isEqualTo(1)
        Truth.assertThat(result.points).isEqualTo(180)
        Truth.assertThat(DailyCodec.decode("garbage")).isNull()
        Truth.assertThat(DailyCodec.decode("2026-13-01;1:100")).isNull()
    }

    @Test
    fun `the text to share has the title, the score and a square per question`() {
        val answers = List(10) { RoundAnswer(correct = it != 3, points = if (it != 3) 150 else 0) }

        val text = dailyShareText("Daily · 1 November", DailyResult(day, answers))

        Truth.assertThat(text).isEqualTo("Daily · 1 November\n9/10 · 1 350\n🟩🟩🟩🟥🟩\n🟩🟩🟩🟩🟩")
    }
}
