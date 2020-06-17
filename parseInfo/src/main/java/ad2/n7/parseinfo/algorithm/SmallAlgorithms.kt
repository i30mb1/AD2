package ad2.n7.parseinfo.algorithm

import java.util.*
import kotlin.math.abs

fun main() {
   removeElement(intArrayOf(3,2,2,3), 3)
}

fun removeElement(nums: IntArray, `val`: Int): Int {
    var size = 0
    for (i in nums.indices) {
        if (nums[i] != `val`) {
            nums[size++] = nums[i]
        }
    }
    return size
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

fun mergeSortedArray(nums1: IntArray, elementsInNums1: Int, nums2: IntArray, elementsInNum2: Int) {
    var nums1Index = elementsInNum2 - 1
    var nums2Index = elementsInNums1 - 1
    var currentIndex = elementsInNum2 + elementsInNums1 - 1
    while (nums1Index >= 0 && nums2Index >= 0) nums1[currentIndex--] = if (nums1[nums1Index] >= nums2[nums2Index]) nums1[nums1Index--] else nums2[nums2Index--]
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

