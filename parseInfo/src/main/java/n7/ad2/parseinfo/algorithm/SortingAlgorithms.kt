package n7.ad2.parseinfo.algorithm


fun main() {
    insertionSorting(intArrayOf(3, 2, 1, 0, 145, 34, 12)).forEach {
        println(it)
    }
}

fun bubbleSorting(array: IntArray): IntArray {
    var needSorting = true
    while (needSorting) {
        needSorting = false
        for (i in 1..array.size - 1) {
            if (array[i] < array[i - 1]) {
                needSorting = true
                array[i - 1] = array[i].also { array[i] = array[i - 1] }
            }
        }
    }
    return array
}

fun selectionSorting(array: IntArray): IntArray {
    for (i in 0..array.size - 1) {
        var indexOfMin = i
        for (j in i..array.size - 1) {
            if (array[j] > array[indexOfMin]) indexOfMin = j
        }
        if (i != indexOfMin) {
            array[i] = array[indexOfMin].also { array[indexOfMin] = array[i] }
        }
    }
    return array
}

fun insertionSorting(array: IntArray): IntArray {
    for (currentIndex in 0 until array.size) {
        val value = array[currentIndex]
        var previousIndex = currentIndex - 1
        while (previousIndex >= 0 && value < array[previousIndex]) {
            array[previousIndex + 1] = array[previousIndex]
            previousIndex--
        }
        array[previousIndex + 1] = value
    }
    return array
}
