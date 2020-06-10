package ad2.n7.parseinfo.algorithm

fun main() {
    val intArray = IntArray(10) { 1 }
    intArray[5] = 0
    println(findMaxConsecutiveOnes(intArray))
}

fun findMaxConsecutiveOnes(nums: IntArray): Int {
    var max = 0
    var cur = 0
    for (number in nums) {
        if (number == 1) cur++
         else { max = maxOf(cur, max); cur = 0 }
    }
    return maxOf(cur, max)
}