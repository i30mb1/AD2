package n7.ad2.drawer.internal.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import n7.ad2.drawer.R
import n7.ad2.drawer.databinding.ItemMenuBinding
import n7.ad2.drawer.internal.domain.vo.VOMenu

class MenuItemHolder private constructor(
    private val binding: ItemMenuBinding,
    private val itemListener: (menuItem: VOMenu) -> Unit,
) : RecyclerView.ViewHolder(binding.root) {

    private val backgroundRipple = ContextCompat.getDrawable(binding.root.context, R.drawable.background_ripple)
    private val transparent = ContextCompat.getDrawable(binding.root.context, R.color.transparent)

    fun bind(item: VOMenu) {
        binding.vRedLine.isVisible = item.isSelected
        binding.tvTitle.text = item.title
        binding.root.foreground = if (item.isEnable) backgroundRipple else transparent
        binding.root.setOnClickListener { itemListener.invoke(item) }
    }

    companion object {
        fun from(
            layoutInflater: LayoutInflater,
            parent: ViewGroup,
            itemListener: (menuItem: VOMenu) -> Unit,
        ): MenuItemHolder {
            val binding = ItemMenuBinding.inflate(layoutInflater, parent, false)
            return MenuItemHolder(binding, itemListener)
        }
    }

}