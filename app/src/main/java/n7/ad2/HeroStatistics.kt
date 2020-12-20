package n7.ad2

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import n7.ad2.databinding.HeroStatisticsBinding
import n7.ad2.utils.extension.toPx


// link about @JVMOverload https://proandroiddev.com/misconception-about-kotlin-jvmoverloads-for-android-view-creation-cb88f432e1fe
@SuppressLint("SetTextI18n")
class HeroStatistics(
    context: Context,
    attributeSet: AttributeSet,
) : ConstraintLayout(context, attributeSet) {

    companion object {
        private const val DEFAULT_SIZE = 60

        data class Statistics(val strength: Double, val agility: Double, val intelligence: Double)
    }

    val binding: HeroStatisticsBinding = HeroStatisticsBinding.inflate(LayoutInflater.from(context), this, true)

    fun setHeroStatistics(statistics: Statistics) {
        binding.tvAgility.text = "${statistics.agility}"
        binding.tvStrength.text = "${statistics.strength}"
        binding.tvIntelligence.text = "${statistics.intelligence}"
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
            MeasureSpec.UNSPECIFIED -> DEFAULT_SIZE.toPx
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