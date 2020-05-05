package n7.ad2

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import n7.ad2.databinding.CustomHeroAttrsBinding

// link about @JVMOverload https://proandroiddev.com/misconception-about-kotlin-jvmoverloads-for-android-view-creation-cb88f432e1fe
@SuppressLint("SetTextI18n")
class CustomHeroAttrs(
        context: Context,
        attributeSet: AttributeSet
) : ConstraintLayout(context, attributeSet) {

    val binding: CustomHeroAttrsBinding = CustomHeroAttrsBinding.inflate(LayoutInflater.from(context), this, true)
    var strength: Int = 0
        set(value) {
            binding.tvAttrStr.text = value.toString()
            field = value
        }
    var strengthInc: Int = 0
        set(value) {
            binding.tvAttrStrInc.text = "+$value"
            field = value
        }
    var agility: Int = 0
        set(value) {
            binding.tvAttrAgi.text = value.toString()
            field = value
        }
    var agilityInc: Int = 0
        set(value) {
            binding.tvAttrAgiInc.text = "+$value"
            field = value
        }
    var intelligence: Int = 0
        set(value) {
            binding.tvAttrInt.text = value.toString()
            field = value
        }
    var intelligenceInc: Int = 0
        set(value) {
            binding.tvAttrIntInc.text = "+$value"
            field = value
        }

    init {
        context.obtainStyledAttributes(attributeSet, R.styleable.CustomHeroAttrs).apply {

            recycle()
        }
    }

    fun getBitmapFromView(): Bitmap {
        val bitmap = Bitmap.createBitmap(this.width, this.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        this.draw(canvas)
        return bitmap
    }

}