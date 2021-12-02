@file:Suppress("unused")

package n7.ad2.android.extension

import android.content.res.Resources
import android.util.TypedValue
import kotlin.math.roundToInt

val Int.dpToPx: Int
    get() = (this * Resources.getSystem().displayMetrics.density.roundToInt())

val Float.spToPx: Float
    get() = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, this, Resources.getSystem().displayMetrics)