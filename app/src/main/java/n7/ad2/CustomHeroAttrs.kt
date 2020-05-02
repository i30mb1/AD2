package n7.ad2

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import n7.ad2.databinding.CustomHeroAttrsBinding

class CustomHeroAttrs(
        context: Context,
        attributeSet: AttributeSet
) : ConstraintLayout(context, attributeSet) {

    val binding: CustomHeroAttrsBinding = CustomHeroAttrsBinding.inflate(LayoutInflater.from(context), this, true)

    init {

    }

}