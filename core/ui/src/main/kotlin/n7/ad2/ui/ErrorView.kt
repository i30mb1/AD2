package n7.ad2.ui

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.core.view.isVisible
import n7.ad2.core.ui.databinding.ErrorBinding

class
ErrorView(context: Context, attributeSet: AttributeSet) : LinearLayout(context, attributeSet) {

    init {
        isVisible = false
        orientation = VERTICAL
        gravity = Gravity.CENTER
    }

    private val binding = ErrorBinding.inflate(LayoutInflater.from(context), this)

    fun setError(text: String?) {
        binding.root.isVisible = text != null
        binding.tv.text = text
    }
}
