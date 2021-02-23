package n7.ad2.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import n7.ad2.AD2Log
import n7.ad2.databinding.ItemLogBinding
import n7.ad2.ui.AD2LoggerAdapter.TextViewHolder
import java.util.ArrayList

class AD2LoggerAdapter : RecyclerView.Adapter<TextViewHolder>() {

    private val list: ArrayList<AD2Log> = ArrayList()

    override fun onBindViewHolder(holder: TextViewHolder, position: Int) = holder.bind(list[position])

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TextViewHolder = TextViewHolder.from(parent)

    override fun getItemCount(): Int = list.size

    fun add(item: AD2Log) = list.add(item)

    class TextViewHolder private constructor(
        private val binding: ItemLogBinding,
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: AD2Log) {
            binding.item = item
            binding.executePendingBindings()
        }

        companion object {
            fun from(
                parent: ViewGroup,
            ): TextViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemLogBinding.inflate(layoutInflater, parent, false)
                return TextViewHolder(binding)
            }
        }
    }
}