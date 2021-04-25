package n7.ad2.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.ObservableBoolean
import androidx.recyclerview.widget.RecyclerView
import n7.ad2.R
import n7.ad2.databinding.ItemMenuBinding

sealed class MenuItem(val title: String) {
    val isSelected = ObservableBoolean(false)
}

class HeroesMenuItem(title: String) : MenuItem(title)
class ItemsMenuItem(title: String) : MenuItem(title)
class NewsMenuItem(title: String) : MenuItem(title)
class TournamentsMenuItem(title: String) : MenuItem(title)
class StreamsMenuItem(title: String) : MenuItem(title)
class GamesMenuItem(title: String) : MenuItem(title)

class MainMenuAdapter(
    private val layoutInflater: LayoutInflater,
    itemListener: (menuItem: MenuItem) -> Unit,
) : RecyclerView.Adapter<MainMenuAdapter.MenuItemHolder>() {

    private val itemListener: (menuItem: MenuItem) -> Unit = { menuItem ->
        menuList.forEach { it.isSelected.set(false) }
        menuList.find { it == menuItem }!!.isSelected.set(true)
        itemListener.invoke(menuItem)
    }
    private val menuList = listOf(
        HeroesMenuItem(layoutInflater.context.getString(R.string.heroes)).apply { isSelected.set(true) },
        ItemsMenuItem(layoutInflater.context.getString(R.string.items)),
        NewsMenuItem(layoutInflater.context.getString(R.string.news)),
        TournamentsMenuItem(layoutInflater.context.getString(R.string.tournaments)),
        StreamsMenuItem(layoutInflater.context.getString(R.string.streams)),
        GamesMenuItem(layoutInflater.context.getString(R.string.games)),
    )

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = MenuItemHolder.from(layoutInflater, parent, itemListener)

    override fun onBindViewHolder(holder: MenuItemHolder, position: Int) = holder.bind(menuList[position])

    override fun getItemCount() = menuList.size

    class MenuItemHolder private constructor(
        private val binding: ItemMenuBinding,
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: MenuItem) {
            binding.item = item
            binding.executePendingBindings()
        }

        companion object {
            fun from(
                layoutInflater: LayoutInflater,
                parent: ViewGroup,
                itemListener: (menuItem: MenuItem) -> Unit,
            ): MenuItemHolder {
                val binding = ItemMenuBinding.inflate(layoutInflater, parent, false)
                binding.itemListener = itemListener
                return MenuItemHolder(binding)
            }
        }

    }

}