package ad2.n7.parseinfo.algorithm

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

