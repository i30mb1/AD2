package n7.ad2.ui

import android.content.res.ColorStateList
import android.content.res.TypedArray
import android.util.AttributeSet
import android.view.View
import android.view.View.OnAttachStateChangeListener
import android.widget.ImageView
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.StyleRes
import androidx.annotation.StyleableRes
import androidx.core.content.res.ResourcesCompat
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

/**
 * Метод можно использовать для запуска некоторой работы внутри CoroutineScope
 * при создании View
 * @link https://www.droidcon.com/2023/10/06/coroutines-flow-android-the-good-parts/
 */
fun View.whileAttachedOnce(
    context: CoroutineContext = EmptyCoroutineContext,
    work: suspend CoroutineScope.() -> Unit,
) {
    var scope: CoroutineScope? = null
    val launchWork = {
        scope = CoroutineScope(context)
        scope?.launch { work() }
    }
    addOnAttachStateChangeListener(object : OnAttachStateChangeListener {
        override fun onViewAttachedToWindow(v: View) {
            launchWork()
        }

        override fun onViewDetachedFromWindow(v: View) {
            scope?.cancel()
        }
    })
    if (isAttachedToWindow) launchWork()
}

/**
 * Usage:
 * withTypedArray(attrs, R.styleable.SquareCheckBoxView) {
 *             text = this.getString(R.styleable.SquareCheckBoxView_check_text) ?: ""
 *             borderColor = this.getColor(R.styleable.SquareCheckBoxView_border_color, resolveColor(R.color.default_border_color))
 *         }
 */
fun View.withTypedArray(
    set: AttributeSet?,
    @StyleableRes attrs: IntArray,
    @AttrRes defStyleAttr: Int = 0,
    @StyleRes defStyleRes: Int = 0,
    action: TypedArray.() -> Unit,
) {
    val typedArray = context.theme.obtainStyledAttributes(
        set, attrs, defStyleAttr, defStyleRes
    )
    action(typedArray)
    typedArray.recycle()
}

@ColorInt
fun View.resolveColor(@ColorRes colorRes: Int): Int =
    ResourcesCompat.getColor(this.context.resources, colorRes, null)

fun ImageView.setTint(@ColorRes color: Int) {
    this.imageTintList = ColorStateList.valueOf(
        resolveColor(color)
    )
}
