package n7.ad2.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import n7.ad2.ui.databinding.ItemHeaderPlayableBinding

class HeaderPlayableViewHolder private constructor(
    private val binding: ItemHeaderPlayableBinding,
    private val onPlayIconClick: (soundUrl: String) -> Unit,
) : RecyclerView.ViewHolder(binding.root) {

    data class Data(val title: String, val isPlaying: Boolean = false, val soundUrl: String? = null)

    fun bind(item: Data) {
        binding.tvTitle.text = item.title

        binding.ivPlay.isVisible = item.soundUrl != null
        binding.ivPlay.isSelected = item.isPlaying
        if (item.soundUrl != null) binding.ivPlay.setOnClickListener { onPlayIconClick(item.soundUrl) }
    }

    fun bind(isPlaying: Boolean) {
        binding.ivPlay.isSelected = isPlaying
    }

    fun clear() {

    }

    companion object {
        fun from(
            layoutInflater: LayoutInflater,
            parent: ViewGroup,
            onPlayIconClick: (soundUrl: String) -> Unit,
        ): HeaderPlayableViewHolder {
            val binding = ItemHeaderPlayableBinding.inflate(layoutInflater, parent, false)
            return HeaderPlayableViewHolder(binding, onPlayIconClick)
        }
    }

}