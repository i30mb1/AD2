package n7.ad2.parseinfo.algorithm
//
//class Solution {
//    fun sortString(s: String): String {
//        val result = StringBuilder()
//        var size = s.length
//        val charsMap = IntArray(s.length) { 0 }
//        s.forEach { charsMap[it - 'a']++ }
//        var index = 0
//        var dir = 1
//        while (size > 0) {
//            when {
//                index < 0 -> dir = 1
//                index == charsMap.size -> dir = -1
//                charsMap[index] > 0 -> {
//                    charsMap[index]--
//                    size--
//                    result.append('a' + index)
//                }
//            }
//            index += dir
//        }
//        return result.toString()
//    }
//}

class Solution {
    fun threeSum(nums: IntArray): List<List<Int>> {
        if(nums.size < 3) return emptyList()
        val result = mutableSetOf<List<Int>>()
        for ((index, value) in nums.withIndex()) {
            for (j in nums) {
                for (k in nums) {
                    if(i + j + k == 0)result.add(listOf(i, j , k))
                }
            }
        }
        return result.toList()
    }
}

fun main() {

}
