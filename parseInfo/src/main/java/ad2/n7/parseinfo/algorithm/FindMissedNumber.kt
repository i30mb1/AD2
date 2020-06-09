package ad2.n7.parseinfo.algorithm

import java.io.FileReader
import java.util.*

// в файле 1_000_000_000 неупорядоченных неотрицательных целых чисел от 0 до 999_999_999
// найти любое пропущенное число в промежутке используя не более 128 KiB памяти
// https://youtu.be/vIp-Q0mD1tk
fun main() {
    val path = "//"
    findFirstMissedNumber(path)
}

fun findFirstMissedNumber(path: String): Int {
    val rangeSize = 1_000_000
    val counters = getFilledCounters(path)

    val rangeIndex = getMissingNumberRangeIndex(counters)
    if (rangeIndex == -1) return -1

    val start = rangeIndex * rangeSize
    val end = start + rangeSize - 1

    val bitSet = getBitVector(path, start, end)

    return getMissingNumber(bitSet, start)
}

fun getFilledCounters(path: String): IntArray {
    val counters = intArrayOf(1000) // int занимает 4байта => следовательно 4 килобайта

    val scanner = Scanner(FileReader(path))
    while (scanner.hasNextInt()) {
        val counterIndex = scanner.nextInt() / 1_000_000
        counters[counterIndex]++
    }
    scanner.close()
    return counters
}

fun getBitVector(path: String, start: Int, end: Int): BitSet {
    val bitSet = BitSet(1_000_000) // массив на  1_000_000 байт будет равен 1_000_000/8 = 125.000 бит => 125 КБ
    val scanner = Scanner(FileReader(path))
    while (scanner.hasNextInt()) {
        val number = scanner.nextInt()
        if (number in start..end) {
            val bitIndex = number - start
            bitSet.set(bitIndex)
        }
    }
    scanner.close()

    return bitSet
}

// получаем номер промежутка с пропущенным числом
fun getMissingNumberRangeIndex(counters: IntArray): Int {
    counters.forEach {
        if (it < 1_000_000) return it
    }
    return -1
}

// функция получения пропущенной цифры в файле
fun getMissingNumber(bitSet: BitSet, start: Int): Int {
    for (i in 0..bitSet.length()) {
        if (!bitSet.get(i)) return i + start
    }
    return -1
}
