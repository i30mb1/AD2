package ad2.n7.parseinfo.algorithm

import kotlin.math.abs

fun main() {
    println(sortedSquares(intArrayOf(-3, 1, 0, 3, 15)))
}

fun findMaxConsecutiveOnes(nums: IntArray): Int {
    var max = 0
    var cur = 0
    for (number in nums) {
        if (number == 1) cur++
        else {
            max = maxOf(cur, max); cur = 0
        }
    }
    return maxOf(cur, max)
}

fun findEvenNumbers(nums: IntArray): Int {
    return nums.map { it.toString() }
            .filter { it.length % 2 == 0 }
            .count()
}

fun findInnerNumberOfNumber(number: Int): Int {
    if (number / 10 == 0) return 1
    return findInnerNumberOfNumber(number / 10) + 1
}

fun sortedSquares(array: IntArray): IntArray {
   return array.map { it * it }.sorted().toIntArray()
}

fun sortedSquares2(array: IntArray): IntArray {
    var leftMarker = 0
    var rightMarker = array.size - 1
    var currentMarker = array.size -1

    val result = IntArray(array.size)

    while (leftMarker <= rightMarker){
        val left = abs(array[leftMarker])
        val right = abs(array[rightMarker])

        result[currentMarker] = if (right > left) {
            rightMarker--
            right * right
        } else {
            leftMarker++
            left * left
        }

        currentMarker--
    }
    return result
}

