@file:Suppress("unused")

package n7.ad2.utils.extension

import android.content.res.Resources

val Int.toPx: Int
    get() = (this * Resources.getSystem().displayMetrics.density.toInt())

val Int.toDp: Int
    get() = (this / Resources.getSystem().displayMetrics.density.toInt())