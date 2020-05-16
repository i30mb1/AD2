package n7.ad2.ui.heroInfo

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import n7.ad2.databinding.ItemDescriptionBinding
import n7.ad2.ui.heroInfo.domain.vo.VODescription

class DescriptionsListAdapter(fragment: HeroInfoFragment) : ListAdapter<VODescription, RecyclerView.ViewHolder>(DiffCallback()) {

    private var popUpListener: View.OnClickListener = View.OnClickListener {
        fragment.showPopup(it)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolderSpellInfoPlain.from(parent, popUpListener)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as ViewHolderSpellInfoPlain).bind(getItem(position))
    }

    class ViewHolderSpellInfoPlain private constructor(
            private val binding: ItemDescriptionBinding,
            private val popUpListener: View.OnClickListener
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: VODescription) {
            binding.model = item
//            binding.hotKey.setOnClickListener(popUpListener)
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup, listener: View.OnClickListener): ViewHolderSpellInfoPlain {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemDescriptionBinding.inflate(layoutInflater, parent, false)
                return ViewHolderSpellInfoPlain(binding, listener)
            }
        }
    }

    private class DiffCallback : DiffUtil.ItemCallback<VODescription>() {

        override fun areItemsTheSame(oldItem: VODescription, newItem: VODescription): Boolean = oldItem.title == newItem.title

        override fun areContentsTheSame(oldItem: VODescription, newItem: VODescription): Boolean = oldItem.body == newItem.body
    }
}


