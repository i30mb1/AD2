package n7.ad2.hero.page.internal.responses

import n7.ad2.hero.page.internal.responses.domain.vo.VOResponse

/** One category of the list: a sticky header and its lines. */
internal class ResponseGroup(val title: String, val items: List<VOResponse.Body>)

/** List position of the header of group [group], when every group is drawn as a header plus [sizes] lines. */
internal fun headerIndex(sizes: List<Int>, group: Int): Int = sizes.take(group).sumOf { it + 1 }

/** The group that list position [itemIndex] belongs to, the chip of that group is the highlighted one. */
internal fun groupAt(sizes: List<Int>, itemIndex: Int): Int {
    var end = 0
    sizes.forEachIndexed { group, size ->
        end += size + 1
        if (itemIndex < end) return group
    }
    return sizes.lastIndex.coerceAtLeast(0)
}

internal fun List<VOResponse>.toGroups(): List<ResponseGroup> {
    val result = mutableListOf<ResponseGroup>()
    var title: String? = null
    val items = mutableListOf<VOResponse.Body>()
    fun flush() {
        val current = title ?: return
        if (items.isNotEmpty()) result.add(ResponseGroup(current, items.toList()))
        items.clear()
    }
    forEach { response ->
        when (response) {
            is VOResponse.Title -> {
                flush()
                title = response.data.title
            }
            is VOResponse.Body -> items.add(response)
        }
    }
    flush()
    return result
}
