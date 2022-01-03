package n7.ad2.streams.internal.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import n7.ad2.android.extension.clear
import n7.ad2.android.extension.load
import n7.ad2.streams.R
import n7.ad2.streams.databinding.ItemListStreamBinding
import n7.ad2.streams.internal.domain.vo.VOStream

internal class StreamSimpleViewHolder(
    private val binding: ItemListStreamBinding,
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(
        vOSimpleStream: VOStream.Simple,
        onStreamClick: (voStream: VOStream) -> Unit,
    ) = binding.apply {
        tvTitle.text = vOSimpleStream.title
        root.setOnClickListener { onStreamClick(vOSimpleStream) }
        ivStreamImage.load(vOSimpleStream.imageUrl, R.drawable.stream_placeholder)
//        ivStreamImage.setImageState()
    }

    fun unbind() = binding.apply {
        root.setOnClickListener(null)
        ivStreamImage.clear()
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