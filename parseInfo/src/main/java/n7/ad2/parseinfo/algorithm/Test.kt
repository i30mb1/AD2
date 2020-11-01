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
    fun maxProfit(prices: IntArray): Int {
        fun calculate(prices: IntArray, s: Int): Int {
            if (s > prices.size) return 0
            var max = 0
            for (index1Price in s..prices.size - 1) {
                var maxProfit = 0
                for (index2Price in index1Price + 1..prices.size - 1) {
                    if (prices[index2Price] > prices[index1Price]) {
                        val profit = calculate(prices, index2Price + 1) + prices[index2Price] - prices[index1Price]
                        if (profit > maxProfit) maxProfit = profit
                    }
                }
                if (maxProfit > max) max = maxProfit
            }
            return max
        }
        return calculate(prices, 0)
    }
}

fun main() {

}
