package n7.ad2.ui.itemInfo.domain.adapter

import n7.ad2.ui.itemInfo.domain.vo.VORecipe

fun String.toVORecipe(): VORecipe {
    return VORecipe(
        "file:///android_asset/items/$this/full.png",
        this
    )
}