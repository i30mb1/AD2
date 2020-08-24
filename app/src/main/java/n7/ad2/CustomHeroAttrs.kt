package n7.ad2

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.core.content.withStyledAttributes
import n7.ad2.databinding.CustomHeroAttrsBinding

// link about @JVMOverload https://proandroiddev.com/misconception-about-kotlin-jvmoverloads-for-android-view-creation-cb88f432e1fe
@SuppressLint("SetTextI18n")
class CustomHeroAttrs(
        context: Context,
        attributeSet: AttributeSet
) : FrameLayout(context, attributeSet) {

    companion object {
        private const val DEFAULT_SIZE = 60
    }

    val binding: CustomHeroAttrsBinding = CustomHeroAttrsBinding.inflate(LayoutInflater.from(context), this, true)
    var strength: Int = 0
        set(value) {
            binding.tvAttrStr.text = "$value+$strengthInc"
            field = value
        }
    var strengthInc: Int = 0
        set(value) {
            binding.tvAttrStr.text = "$strength+$value"
            field = value
        }
    var agility: Int = 0
        set(value) {
            binding.tvAttrAgi.text = "$value+$agilityInc"
            field = value
        }
    var agilityInc: Int = 0
        set(value) {
            binding.tvAttrAgi.text = "$agility+$value"
            field = value
        }
    var intelligence: Int = 0
        set(value) {
            binding.tvAttrInt.text = "$value+$intelligenceInc"
            field = value
        }
    var intelligenceInc: Int = 0
        set(value) {
            binding.tvAttrInt.text = "$intelligence+$value"
            field = value
        }

    init {
        context.withStyledAttributes(attributeSet, R.styleable.CustomHeroAttrs) {

        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

//        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
//        val heightSize = MeasureSpec.getSize(heightMeasureSpec)
//        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
//        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
//
//        val initSize = resolveDefaultSize(widthMeasureSpec)
//        setMeasuredDimension(initSize, resolveDefaultSize(widthMeasureSpec))
    }

//    override fun onSaveInstanceState(): Parcelable? {
//        val superState: Parcelable? = super.onSaveInstanceState()
//        superState?.let {
//            val state = SavedState(superState)
//            state.strength = this.strength
//            return state
//        } ?: kotlin.run {
//            return superState
//        }
//    }
//
//    override fun onRestoreInstanceState(state: Parcelable?) {
//        when (state) {
//            is SavedState -> {
//                super.onRestoreInstanceState(state.superState)
//                this.strength = state.strength
//            }
//            else -> {
//                super.onRestoreInstanceState(state)
//            }
//        }
//    }

//    internal class SavedState : androidx.customview.view.AbsSavedState {
//        var strength: Int = 0
//
//        constructor(superState: Parcelable) : super(superState)
//
//        constructor(source: Parcel, loader: ClassLoader?) : super(source, loader) {
//            strength = source.readInt()
//        }
//
//        override fun writeToParcel(out: Parcel, flags: Int) {
//            super.writeToParcel(out, flags)
//            out.writeInt(strength)
//        }
//
//        companion object {
//            @JvmField
//            val CREATOR: Parcelable.Creator<SavedState> = object : Parcelable.ClassLoaderCreator<SavedState> {
//
//                override fun newArray(size: Int): Array<SavedState> {
//                    return newArray(size)
//                }
//
//                override fun createFromParcel(source: Parcel, loader: ClassLoader?): SavedState {
//                    return SavedState(source, loader)
//                }
//
//                override fun createFromParcel(source: Parcel): SavedState {
//                    return SavedState(source, null)
//                }
//            }
//
//
//        }
//
//    }

    private fun resolveDefaultSize(spec: Int): Int {
        return when (MeasureSpec.getMode(spec)) {
            MeasureSpec.UNSPECIFIED -> {
                context.dpToPx(DEFAULT_SIZE).toInt()
            }
            MeasureSpec.AT_MOST -> MeasureSpec.getSize(spec)
            MeasureSpec.EXACTLY -> MeasureSpec.getSize(spec)
            else -> MeasureSpec.getSize(spec)
        }
    }

    private fun getBitmapFromView(): Bitmap {
        // instead of it you can use View.drawToBitmap
        val bitmap = Bitmap.createBitmap(this.width, this.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        this.draw(canvas)
        return bitmap
    }

}

fun Context.dpToPx(dp: Int): Float {
    return dp.toFloat() * this.resources.displayMetrics.density
}