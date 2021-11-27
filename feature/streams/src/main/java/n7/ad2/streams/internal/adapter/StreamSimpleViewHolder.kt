package n7.ad2.streams.internal.adapter

import ad2.n7.android.extension.load
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import n7.ad2.streams.R
import n7.ad2.streams.databinding.ItemListStreamBinding
import n7.ad2.streams.internal.domain.vo.VOStream

class StreamSimpleViewHolder(
    private val binding: ItemListStreamBinding,
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(
        vOSimpleStream: VOStream.Simple,
        onStreamClick: (voStream: VOStream) -> Unit,
    ) = binding.apply {
        binding.tvTitle.text = vOSimpleStream.title
        binding.root.setOnClickListener { onStreamClick(vOSimpleStream) }
        binding.ivStreamImage.load(vOSimpleStream.imageUrl, R.drawable.stream_placeholder)
    }

    fun unbind() {
        binding.root.setOnClickListener(null)
//        binding.ivStreamImage.clear()
    }

    companion object {
        fun from(
            layoutInflater: LayoutInflater,
            parent: ViewGroup,
        ): StreamSimpleViewHolder {
            val binding = ItemListStreamBinding.inflate(layoutInflater, parent, false)
            return StreamSimpleViewHolder(binding)
        }
    }

}