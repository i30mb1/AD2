package n7.ad2.drawer.internal.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import n7.ad2.drawer.internal.domain.vo.VOMenu


class MainMenuListAdapter(
    private val layoutInflater: LayoutInflater,
    itemListener: (menuItem: VOMenu) -> Unit,
) : ListAdapter<VOMenu, MenuItemHolder>(DiffCallback()) {

    private val itemListener: (menuItem: VOMenu) -> Unit = { menuItem -> itemListener(menuItem) }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = MenuItemHolder.from(layoutInflater, parent, itemListener)

    override fun onBindViewHolder(holder: MenuItemHolder, position: Int) = holder.bind(getItem(position))

    private class DiffCallback : DiffUtil.ItemCallback<VOMenu>() {
        override fun areItemsTheSame(oldItem: VOMenu, newItem: VOMenu) = oldItem.type == newItem.type
        override fun areContentsTheSame(oldItem: VOMenu, newItem: VOMenu) = oldItem.isSelected == newItem.isSelected
    }

}