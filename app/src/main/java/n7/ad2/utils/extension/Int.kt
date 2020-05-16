@file:Suppress("unused")

package n7.ad2.utils.extension

import android.content.res.Resources

val Int.toPx: Float
    get() = (this * Resources.getSystem().displayMetrics.density)

val Int.toDp: Float
    get() = (this / Resources.getSystem().displayMetrics.density)