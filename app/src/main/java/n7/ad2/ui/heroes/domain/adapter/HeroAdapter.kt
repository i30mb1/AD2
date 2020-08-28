package n7.ad2.ui.heroes.domain.adapter

import n7.ad2.data.source.local.model.LocalHero
import n7.ad2.ui.heroes.domain.vo.VOHero
import java.io.File

fun LocalHero.toVo() : VOHero {
    return VOHero().also {
        it.name = name
        it.image = "file:///android_asset/$assetsPath/full.png"
        it.viewedByUser = viewedByUser
    }
}