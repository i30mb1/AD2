package n7.ad2.drawer.internal.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import n7.ad2.app.logger.model.AppLog
import n7.ad2.drawer.internal.adapter.LoggerAdapter.TextViewHolder
import n7.ad2.feature.drawer.databinding.ItemLogBinding

internal class LoggerAdapter(
    private val layoutInflater: LayoutInflater,
) : RecyclerView.Adapter<TextViewHolder>() {

    private val list: ArrayList<AppLog> = ArrayList()

    override fun onBindViewHolder(holder: TextViewHolder, position: Int) = holder.bind(list[position])

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TextViewHolder = TextViewHolder.from(parent, layoutInflater)

    override fun getItemCount(): Int = list.size

    fun clear() {
        list.clear()
        notifyDataSetChanged()
    }

    fun add(item: AppLog) {
        list.add(item)
        notifyItemInserted(list.lastIndex)
    }

    class TextViewHolder private constructor(
        private val binding: ItemLogBinding,
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: AppLog) {
            binding.tvText.text = item.message
        }

        companion object {
            fun from(parent: ViewGroup, layoutInflater: LayoutInflater): TextViewHolder {
                val binding = ItemLogBinding.inflate(layoutInflater, parent, false)
                return TextViewHolder(binding)
            }
        }
    }
}
