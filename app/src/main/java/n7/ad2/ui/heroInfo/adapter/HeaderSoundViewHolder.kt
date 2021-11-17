package n7.ad2.ui.heroInfo.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import n7.ad2.databinding.ItemHeaderSoundBinding
import n7.ad2.ui.heroInfo.domain.vo.VOHeroInfoHeaderSound

class HeaderSoundViewHolder private constructor(
    private val binding: ItemHeaderSoundBinding,
    private val onPlayIconClickListener: (model: VOHeroInfoHeaderSound) -> Unit,
    private val onKeyClickListener: (key: String) -> Unit,
) : RecyclerView.ViewHolder(binding.root) {


    fun bind(item: VOHeroInfoHeaderSound) {
        binding.tvHotKey.text = item.hotkey
        binding.tvLegacyKey.text = item.legacyKey
        binding.tvTitle.text = item.title

        binding.ivPlay.setOnClickListener { onPlayIconClickListener(item) }
        binding.tvHotKey.setOnClickListener { onKeyClickListener(item.hotkey) }
        binding.tvLegacyKey.setOnClickListener { onKeyClickListener(item.legacyKey) }
    }

    fun clear() {

    }

    companion object {
        fun from(
            layoutInflater: LayoutInflater,
            parent: ViewGroup,
            playClickListener: (model: VOHeroInfoHeaderSound) -> Unit,
            keyClickListener: (key: String) -> Unit,
        ): HeaderSoundViewHolder {
            val binding = ItemHeaderSoundBinding.inflate(layoutInflater, parent, false)
            return HeaderSoundViewHolder(
                binding,
                playClickListener,
                keyClickListener,
            )
        }
    }

}