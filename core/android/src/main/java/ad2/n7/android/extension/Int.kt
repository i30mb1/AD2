@file:Suppress("unused")

package ad2.n7.android.extension

import android.content.res.Resources

val Int.toPx: Int
    get() = (this * Resources.getSystem().displayMetrics.density.toInt())

val Int.toPxF: Float
    get() = (this * Resources.getSystem().displayMetrics.density)

val Int.toDp: Int
    get() = (this / Resources.getSystem().displayMetrics.density.toInt())