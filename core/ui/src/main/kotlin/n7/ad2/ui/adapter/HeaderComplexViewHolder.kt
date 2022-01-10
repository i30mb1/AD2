package n7.ad2.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import n7.ad2.ui.databinding.ItemHeaderComplexBinding

class HeaderComplexViewHolder private constructor(
    private val binding: ItemHeaderComplexBinding,
    private val onPlayIconClick: (soundUrl: String) -> Unit,
    private val onKeyIconClick: (key: String) -> Unit,
) : RecyclerView.ViewHolder(binding.root) {

    data class Data(val title: String, val isPlaying: Boolean = false, val soundUrl: String? = null)


    fun bind(item: Data) {
//        binding.tvHotKey.text = item.hotkey
//        binding.tvLegacyKey.text = item.legacyKey
        binding.tvTitle.text = item.title

        binding.ivPlay.isVisible = item.soundUrl != null
        binding.ivPlay.isSelected = item.isPlaying
        if (item.soundUrl != null) binding.ivPlay.setOnClickListener { onPlayIconClick(item.soundUrl) }
//        binding.tvHotKey.setOnClickListener { onKeyIconClick(item.hotkey) }
//        binding.tvLegacyKey.setOnClickListener { onKeyClickListener(item.legacyKey) }
    }

    fun clear() {

    }

    companion object {
        fun from(
            layoutInflater: LayoutInflater,
            parent: ViewGroup,
            onPlayIconClick: (soundUrl: String) -> Unit,
            onKeyIconClick: (key: String) -> Unit,
        ): HeaderComplexViewHolder {
            val binding = ItemHeaderComplexBinding.inflate(layoutInflater, parent, false)
            return HeaderComplexViewHolder(binding, onPlayIconClick, onKeyIconClick)
        }
    }

}