package ad2.n7.parseinfo.algorithm

import java.util.*
import kotlin.math.abs

fun main() {
    duplicateEachOccurrenceOfZeroShiftingTheRemainingToRight2(intArrayOf(1,0,2,3,0,4,5,0))
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

fun duplicateEachOccurrenceOfZeroShiftingTheRemainingToRight(arr: IntArray) {
    val q: Queue<Int> = LinkedList()

    for (i in arr.indices) {
        q.add(arr[i])
        if (arr[i] == 0) q.add(0)
        arr[i] = q.remove()
    }
}

fun duplicateEachOccurrenceOfZeroShiftingTheRemainingToRight2(arr: IntArray) {
        var i = 0
        while (i < arr.size) {
            if (arr[i] == 0) {
                for (j in arr.size - 1 downTo i + 1) {
                    arr[j] = arr[j - 1]
                }
                i++
            }
            i++
        }
    arr.forEach {
        println(it)
    }
}

fun sortedSquares(array: IntArray): IntArray {
    return array.map { it * it }.sorted().toIntArray()
}

fun sortedSquares2(array: IntArray): IntArray {
    var leftMarker = 0
    var rightMarker = array.size - 1
    var currentMarker = array.size - 1

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

