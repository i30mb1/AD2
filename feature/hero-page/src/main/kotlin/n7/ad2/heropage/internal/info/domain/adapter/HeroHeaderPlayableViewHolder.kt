package n7.ad2.heropage.internal.info.domain.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import n7.ad2.heropage.databinding.ItemHeroHeaderPlayableBinding
import n7.ad2.heropage.internal.info.domain.vo.VOHeroInfo

class HeroHeaderPlayableViewHolder(
    private val binding: ItemHeroHeaderPlayableBinding,
    private val onPlayIconClick: (soundUrl: String) -> Unit,
    private val onKeyClickListener: (key: String) -> Unit,
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(data: VOHeroInfo.HeaderSound) {
        binding.tvTitle.text = data.title
        binding.ivPlay.isVisible = data.soundUrl != null
        binding.ivPlay.isSelected = data.isPlaying
        if (data.soundUrl != null) {
            binding.ivPlay.setOnClickListener { onPlayIconClick(data.soundUrl) }
        }
        binding.tvHotKey.isVisible = data.hotkey != null
        if (data.hotkey != null) {
            binding.tvHotKey.setOnClickListener { onKeyClickListener(data.hotkey) }
            binding.tvHotKey.text = data.hotkey
        }
        binding.tvLegacyKey.isVisible = data.legacyKey != null
        if (data.legacyKey != null) {
            binding.tvLegacyKey.setOnClickListener { onKeyClickListener(data.legacyKey) }
            binding.tvLegacyKey.text = data.legacyKey
        }
    }

    companion object {
        fun from(
            layoutInflater: LayoutInflater,
            parent: ViewGroup,
            onPlayIconClick: (soundUrl: String) -> Unit,
            onKeyClickListener: (key: String) -> Unit,
        ): HeroHeaderPlayableViewHolder {
            val binding = ItemHeroHeaderPlayableBinding.inflate(layoutInflater, parent, false)
            return HeroHeaderPlayableViewHolder(binding, onPlayIconClick, onKeyClickListener)
        }
    }

}