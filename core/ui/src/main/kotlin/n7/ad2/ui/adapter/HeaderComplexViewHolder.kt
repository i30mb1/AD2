package n7.ad2.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import n7.ad2.ui.databinding.ItemHeaderComplexBinding

class HeaderComplexViewHolder private constructor(
    private val binding: ItemHeaderComplexBinding,
    private val onPlayIconClickListener: (model: Data) -> Unit,
    private val onQuickKeyClickListener: (key: String) -> Unit,
) : RecyclerView.ViewHolder(binding.root) {

    data class Data(val title: String, val soundUrl: String? = null)


    fun bind(item: Data) {
//        binding.tvHotKey.text = item.hotkey
//        binding.tvLegacyKey.text = item.legacyKey
        binding.tvTitle.text = item.title

        binding.ivPlay.isVisible = item.soundUrl != null
        if (item.soundUrl != null) binding.ivPlay.setOnClickListener { onPlayIconClickListener(item) }
//        binding.tvHotKey.setOnClickListener { onKeyClickListener(item.hotkey) }
//        binding.tvLegacyKey.setOnClickListener { onKeyClickListener(item.legacyKey) }
    }

    fun clear() {

    }

    companion object {
        fun from(
            layoutInflater: LayoutInflater,
            parent: ViewGroup,
            playClickListener: (model: Data) -> Unit,
            keyClickListener: (key: String) -> Unit,
        ): HeaderComplexViewHolder {
            val binding = ItemHeaderComplexBinding.inflate(layoutInflater, parent, false)
            return HeaderComplexViewHolder(binding, playClickListener, keyClickListener)
        }
    }

}