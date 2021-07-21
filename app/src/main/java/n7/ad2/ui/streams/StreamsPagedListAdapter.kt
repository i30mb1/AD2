package n7.ad2.ui.streams

import android.os.Handler
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.robinhood.ticker.TickerUtils
import n7.ad2.R
import n7.ad2.data.source.remote.model.Stream
import n7.ad2.databinding.ItemListStreamBinding
import n7.ad2.ui.heroResponse.domain.vo.VOResponse
import java.util.Random

class StreamsPagedListAdapter: PagingDataAdapter<Stream, StreamsPagedListAdapter.ViewHolder>(DiffCallback()) {

    private val view = intArrayOf(-2, -1, 0, 1, 2, 3)
    private val duration = longArrayOf(2000, 3000, 4000, 3500, 2500)
    private var inflater: LayoutInflater? = null
    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ViewHolder {
        if (inflater == null) inflater = LayoutInflater.from(viewGroup.context)
        val binding: ItemListStreamBinding = DataBindingUtil.inflate(inflater!!, R.layout.item_list_stream, viewGroup, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, i: Int) {}
    inner class ViewHolder(var binding: ItemListStreamBinding) : RecyclerView.ViewHolder(binding.root) {
        var handler: Handler
        private fun bindTo() {}
        private fun clear() {
            binding.ivItemListStream.setImageResource(R.drawable.streams_placeholder)
            binding.tvItemListStreamTitle.text = ""
            binding.tvItemListStreamSummary.text = ""
        }

        init {
            binding.tvItemListStreamViewers.setCharacterList(TickerUtils.getDefaultNumberList())
            handler = Handler()
            handler.post(object : Runnable {
                override fun run() {
                    try {
                        val value = Integer.valueOf(binding.tvItemListStreamViewers.text)
                        val randomValue = +view[Random().nextInt(view.size - 1)]
                        if (value + randomValue >= 0) binding.tvItemListStreamViewers.text = (value + randomValue).toString()
                        handler.postDelayed(this, duration[Random().nextInt(duration.size - 1)])
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            })
        }
    }

    private class DiffCallback : DiffUtil.ItemCallback<Stream>() {
        override fun areItemsTheSame(oldItem: Stream, newItem: Stream) = oldItem::class == newItem::class
        override fun areContentsTheSame(oldItem: Stream, newItem: Stream) = oldItem == newItem
    }
}