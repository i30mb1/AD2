package n7.ad2

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import n7.ad2.databinding.CustomHeroAttrsBinding

// link about @JVMOverload https://proandroiddev.com/misconception-about-kotlin-jvmoverloads-for-android-view-creation-cb88f432e1fe
class CustomHeroAttrs(
        context: Context,
        attributeSet: AttributeSet
) : ConstraintLayout(context, attributeSet) {

    val binding: CustomHeroAttrsBinding = CustomHeroAttrsBinding.inflate(LayoutInflater.from(context), this, true)
    private var strength: Int
    private var strengthInc: Int
    private var agility: Int
    private var agilityInc: Int
    private var intelligence: Int
    private var intelligenceInc: Int

    init {
        context.obtainStyledAttributes(attributeSet, R.styleable.CustomHeroAttrs).apply {
            strength = getInt(R.styleable.CustomHeroAttrs_strength, 0)
            strengthInc = getInt(R.styleable.CustomHeroAttrs_strengthInc, 0)
            agility = getInt(R.styleable.CustomHeroAttrs_agility, 0)
            agilityInc = getInt(R.styleable.CustomHeroAttrs_agilityInc, 0)
            intelligence = getInt(R.styleable.CustomHeroAttrs_intelligence, 0)
            intelligenceInc = getInt(R.styleable.CustomHeroAttrs_intelligenceInc, 0)
            recycle()
        }

        binding.tvAttrStr.text = strength.toString()
        binding.tvAttrStrInc.text = strengthInc.toString()
        binding.tvAttrAgi.text = agility.toString()
        binding.tvAttrAgiInc.text = agilityInc.toString()
        binding.tvAttrInt.text = intelligence.toString()
        binding.tvAttrIntInc.text = intelligenceInc.toString()
    }

    fun getBitmapFromView(): Bitmap {
        val bitmap = Bitmap.createBitmap(this.width, this.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        this.draw(canvas)
        return bitmap
    }

}