package ad2.n7.parseinfo.algorithm


fun main() {
    bubbleSorting(intArrayOf(3,2,1,0,145,34,12)).forEach {
        println(it)
    }
}

fun bubbleSorting(array: IntArray): IntArray {
    var sorted = true
    while (sorted) {
        sorted = false
        for (i in 1 until array.size) {
            if (array[i] < array[i - 1]) {
                sorted = true
                array[i - 1] = array[i].also { array[i] = array[i - 1] }
            }
        }
    }
    return array
}

