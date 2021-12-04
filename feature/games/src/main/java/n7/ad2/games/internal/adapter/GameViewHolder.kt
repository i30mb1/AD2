package n7.ad2.games.internal.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import n7.ad2.games.databinding.ItemGameBinding
import n7.ad2.games.internal.data.Players
import n7.ad2.games.internal.data.VOGame

internal class GameViewHolder private constructor(
    private val binding: ItemGameBinding,
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(
        game: VOGame,
        onGameClickListener: (game: VOGame, players: Players) -> Unit,
    ) = binding.apply {
        name.text = game.name
        p2.isVisible = game !is VOGame.CanYouBuyIt
        p1.setOnClickListener { onGameClickListener(game, Players.One) }
        p2.setOnClickListener { onGameClickListener(game, Players.Two) }
    }

    companion object {
        fun from(
            layoutInflater: LayoutInflater,
            parent: ViewGroup,
        ): GameViewHolder {
            val binding = ItemGameBinding.inflate(layoutInflater, parent, false)
            return GameViewHolder(binding)
        }
    }

}