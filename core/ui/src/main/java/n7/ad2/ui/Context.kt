package n7.ad2.android.extension

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import androidx.annotation.AttrRes

@SuppressLint("Recycle")
fun Context.themeColor(@AttrRes themeAttrId: Int): Int {
    return obtainStyledAttributes(intArrayOf(themeAttrId)).use { it.getColor(0, Color.RED) }
}