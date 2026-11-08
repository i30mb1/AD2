package n7.ad2.streams.internal.stream.compose

private const val NANOS_IN_SECOND = 1_000_000_000f

/** A message flying over the video: it enters at the right edge at [startNanos] and moves left at [speed] pixels a second. */
internal class TickerRun<T>(val message: T, val lane: Int, val width: Float, val startNanos: Long, val speed: Float) {
    fun x(screenWidth: Float, nowNanos: Long): Float = screenWidth - speed * (nowNanos - startNanos) / NANOS_IN_SECOND
}

/**
 * Chat messages flying from right to left in rows over the video, like on Niconico. A row takes the next message once
 * the previous one has fully entered, so messages never overlap.
 * A quiet chat goes to the emptiest of [BASE_LANES] rows instead of crawling along the top one. When messages pile up
 * they move faster and two more rows open; a message waits [MAX_WAIT_NANOS] at most and only the last [MAX_WAITING]
 * wait, so what flies by is what the chat is writing now, not what it wrote a while ago.
 */
internal class Ticker<T>(private val screenWidth: Float, private val gap: Float) {

    private class Waiting<T>(val message: T, val offeredNanos: Long)

    private val waiting = ArrayDeque<Waiting<T>>()
    private val last = arrayOfNulls<TickerRun<T>>(MAX_LANES)

    fun offer(message: T, nowNanos: Long) {
        waiting.addLast(Waiting(message, nowNanos))
        if (waiting.size > MAX_WAITING) waiting.removeFirst()
    }

    /** Starts the waiting messages the rows have room for, [width] measures a message in pixels. */
    fun start(nowNanos: Long, width: (T) -> Float): List<TickerRun<T>> {
        while (waiting.isNotEmpty() && nowNanos - waiting.first().offeredNanos > MAX_WAIT_NANOS) waiting.removeFirst()
        val started = mutableListOf<TickerRun<T>>()
        while (waiting.isNotEmpty()) {
            val lanes = if (waiting.size > BASE_LANES) MAX_LANES else BASE_LANES
            val lane = (0 until lanes).filter { isFree(it, nowNanos) }.maxByOrNull { room(it, nowNanos) } ?: break
            val speed = speedIn(lane, nowNanos)
            val message = waiting.removeFirst().message
            val run = TickerRun(message, lane, width(message), nowNanos, speed)
            last[lane] = run
            started += run
        }
        return started
    }

    private fun isFree(lane: Int, nowNanos: Long): Boolean = room(lane, nowNanos) >= gap

    /** Free space behind the last message of the row, an empty row has all of it. */
    private fun room(lane: Int, nowNanos: Long): Float {
        val previous = last[lane] ?: return Float.MAX_VALUE
        return screenWidth - (previous.x(screenWidth, nowNanos) + previous.width)
    }

    /** Faster the more messages wait, but never so fast that the message catches up with the one ahead before it leaves. */
    private fun speedIn(lane: Int, nowNanos: Long): Float {
        val wanted = screenWidth / SCREEN_CROSS_SECONDS * (1 + waiting.size / BOOST_PER_WAITING).coerceAtMost(MAX_BOOST)
        val previous = last[lane] ?: return wanted
        val tail = previous.x(screenWidth, nowNanos) + previous.width
        if (tail <= 0) return wanted
        val leavesInSeconds = tail / previous.speed
        val catchUp = (screenWidth - gap) / leavesInSeconds
        return minOf(wanted, maxOf(previous.speed, catchUp))
    }

    companion object {
        /** Rows the chat may take at the top of the video. */
        const val MAX_LANES = 5
        private const val BASE_LANES = 3
        private const val MAX_WAITING = 8
        private const val MAX_WAIT_NANOS = 3_000_000_000L
        private const val SCREEN_CROSS_SECONDS = 6f
        private const val BOOST_PER_WAITING = 10f
        private const val MAX_BOOST = 2f
    }
}
