package n7.ad2.ui.heroInfo

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import n7.ad2.base.VOPopUpListener
import n7.ad2.databinding.ItemDescriptionBinding
import n7.ad2.ui.heroInfo.domain.vo.VODescription

class DescriptionsListAdapter(private val fragment: HeroInfoFragment) : ListAdapter<VODescription, RecyclerView.ViewHolder>(DiffCallback()) {

    private var listener: VOPopUpListener<String> = object : VOPopUpListener<String> {

        override fun onClickListener(view: View, text: String) {
            fragment.showPopup(view, text)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolderSpellInfoPlain.from(parent, listener, fragment)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as ViewHolderSpellInfoPlain).bind(getItem(position))
    }

    class ViewHolderSpellInfoPlain private constructor(
            private val binding: ItemDescriptionBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: VODescription) {
            binding.model = item
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup, listener: VOPopUpListener<String>, fragment: HeroInfoFragment): ViewHolderSpellInfoPlain {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemDescriptionBinding.inflate(layoutInflater, parent, false)
                binding.listener = listener
                binding.fragment = fragment
                return ViewHolderSpellInfoPlain(binding)
            }
        }
    }

    private class DiffCallback : DiffUtil.ItemCallback<VODescription>() {

        override fun areItemsTheSame(oldItem: VODescription, newItem: VODescription): Boolean = true

        override fun areContentsTheSame(oldItem: VODescription, newItem: VODescription): Boolean = oldItem.body == newItem.body
    }
}


