package n7.ad2.utils.extension

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import androidx.annotation.AttrRes
import androidx.core.content.res.use

@SuppressLint("Recycle")
fun Context.themeColor(@AttrRes themeAttrId: Int): Int {
    return obtainStyledAttributes(intArrayOf(themeAttrId)).use { it.getColor(0, Color.RED) }
}