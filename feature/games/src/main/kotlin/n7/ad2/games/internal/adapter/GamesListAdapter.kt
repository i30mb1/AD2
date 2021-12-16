package n7.ad2.games.internal.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import n7.ad2.games.internal.data.Players
import n7.ad2.games.internal.data.VOGame

internal class GamesListAdapter(
    private val layoutInflater: LayoutInflater,
    private val onGameClickListener: (game: VOGame, players: Players) -> Unit,
) : ListAdapter<VOGame, GameViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = GameViewHolder.from(layoutInflater, parent)

    override fun onBindViewHolder(holder: GameViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item, onGameClickListener)
    }

    private class DiffCallback : DiffUtil.ItemCallback<VOGame>() {
        override fun areItemsTheSame(oldItem: VOGame, newItem: VOGame) = oldItem::class == newItem::class
        override fun areContentsTheSame(oldItem: VOGame, newItem: VOGame) = oldItem == newItem
    }

}