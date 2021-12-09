package n7.ad2.games.internal

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import n7.ad2.android.DrawerPercentListener
import n7.ad2.android.findDependencies
import n7.ad2.games.R
import n7.ad2.games.databinding.FragmentGamesBinding
import n7.ad2.games.internal.adapter.GamesItemDecorator
import n7.ad2.games.internal.adapter.GamesListAdapter
import n7.ad2.games.internal.data.Players
import n7.ad2.games.internal.data.VOGame
import n7.ad2.games.internal.di.DaggerGamesComponent
import n7.ad2.ktx.viewModel
import n7.ad2.logger.AD2Logger
import javax.inject.Inject

internal class GamesFragment : Fragment(R.layout.fragment_games) {

    companion object {
        fun getInstance() = GamesFragment()
    }

    @Inject lateinit var gamesViewModelFactory: GamesViewModel.Factory
    @Inject lateinit var logger: AD2Logger

    private var _binding: FragmentGamesBinding? = null
    private val binding: FragmentGamesBinding get() = _binding!!
    private val gamesItemDecorator = GamesItemDecorator()
    private val viewModel: GamesViewModel by viewModel { gamesViewModelFactory.create() }
    private val onGameClickListener: (game: VOGame, players: Players) -> Unit = { game, players -> }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        DaggerGamesComponent.factory().create(findDependencies()).inject(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentGamesBinding.bind(view)
        setupRecyclerView()
        setupInsets()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun setupRecyclerView() {
        val linearLayoutManager = LinearLayoutManager(requireContext())
        val gamesListAdapter = GamesListAdapter(layoutInflater, onGameClickListener)
        binding.rv.layoutManager = linearLayoutManager
        binding.rv.adapter = gamesListAdapter
        binding.rv.addItemDecoration(gamesItemDecorator)
        viewModel.games.observe(viewLifecycleOwner, gamesListAdapter::submitList)
    }

    private fun setupInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.rv) { _, insets ->
            val statusBarsInsets = insets.getInsets(WindowInsetsCompat.Type.statusBars())
            val navigationBarsInsets = insets.getInsets(WindowInsetsCompat.Type.navigationBars())
            gamesItemDecorator.statusBarsInsets = statusBarsInsets.top
            gamesItemDecorator.navigationBarsInsets = navigationBarsInsets.bottom
            insets
        }
        (parentFragment as DrawerPercentListener).setDrawerPercentListener { percent ->
            gamesItemDecorator.percent = percent
            binding.rv.invalidateItemDecorations()
        }
    }

}