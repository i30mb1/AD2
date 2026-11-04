package n7.ad2.games.internal

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.edit
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import javax.inject.Inject
import kotlinx.coroutines.launch
import n7.ad2.android.DependenciesMap
import n7.ad2.android.DrawerPercentListener
import n7.ad2.android.HasDependencies
import n7.ad2.android.findDependencies
import n7.ad2.android.getMainFragmentNavigator
import n7.ad2.app.logger.Logger
import n7.ad2.core.ui.content
import n7.ad2.feature.games.mix.ui.R
import n7.ad2.games.internal.compose.GamesScreen
import n7.ad2.games.internal.data.GameVO
import n7.ad2.games.internal.di.DaggerGamesComponent
import n7.ad2.games.internal.games.invoker.InvokerFragment
import n7.ad2.games.internal.games.invoker.InvokerViewModel
import n7.ad2.games.internal.games.mid.MidFragment
import n7.ad2.games.internal.games.mid.MidViewModel
import n7.ad2.games.internal.games.show.ShowFragment
import n7.ad2.games.internal.games.xo.XoFragment
import n7.ad2.games.internal.games.xo.XoViewModel
import n7.ad2.games.room.GameMode
import n7.ad2.ktx.viewModel

internal class GamesFragment @JvmOverloads constructor(override var dependenciesMap: DependenciesMap = emptyMap()) :
    Fragment(),
    HasDependencies {

    @Inject lateinit var gamesViewModelFactory: GamesViewModel.Factory

    @Inject lateinit var logger: Logger

    private val viewModel: GamesViewModel by viewModel { gamesViewModelFactory.create() }

    private val wifiDirectPermissions = wifiDirectPermissions(Build.VERSION.SDK_INT)
    private val permissionRequest = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { granted ->
        viewModel.onWifiDirectPermission(granted[wifiDirectPermissions.first()] == true)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        DaggerGamesComponent.factory().create(findDependencies()).inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View = content {
        GamesScreen(viewModel, parentFragment as? DrawerPercentListener, ::onGameClicked, ::onDailyClicked)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (ContextCompat.checkSelfPermission(requireContext(), wifiDirectPermissions.first()) == PackageManager.PERMISSION_GRANTED) {
            viewModel.onWifiDirectPermission(true)
        } else if (savedInstanceState == null) {
            askWifiDirectPermission()
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.openGame.collect { game -> open(gameFragment(game.gameId, game.mode)) }
            }
        }
    }

    private fun askWifiDirectPermission() {
        val preferences = requireContext().getSharedPreferences("games_wifi_direct", Context.MODE_PRIVATE)
        if (!wifiDirectNeedsExplanation(Build.VERSION.SDK_INT, preferences.getBoolean(KEY_EXPLAINED, false))) {
            permissionRequest.launch(wifiDirectPermissions)
            return
        }
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.wifi_direct_explanation_title)
            .setMessage(R.string.wifi_direct_explanation)
            .setCancelable(false)
            .setNegativeButton(R.string.wifi_direct_explanation_later) { _, _ -> viewModel.onWifiDirectPermission(false) }
            .setPositiveButton(R.string.wifi_direct_explanation_continue) { _, _ ->
                preferences.edit { putBoolean(KEY_EXPLAINED, true) }
                permissionRequest.launch(wifiDirectPermissions)
            }
            .show()
    }

    // виден только список: в игре телефон не объявляется (сессия остается)
    override fun onStart() {
        super.onStart()
        viewModel.setVisible(true)
    }

    override fun onStop() {
        viewModel.setVisible(false)
        super.onStop()
    }

    /** Из списка игра открывается соло; по сети игру выбирает хост комнаты, она откроется у всех сама */
    private fun onGameClicked(game: GameVO) = open(gameFragment(game.id, GameMode.SOLO))

    private fun gameFragment(gameId: String, mode: GameMode): Fragment = when (gameId) {
        MidViewModel.GAME_ID -> MidFragment.getInstance(mode)
        InvokerViewModel.GAME_ID -> InvokerFragment.getInstance(mode)
        XoViewModel.GAME_ID -> XoFragment.getInstance(mode)
        else -> ShowFragment.getInstance(mode)
    }

    private fun onDailyClicked() = open(ShowFragment.getInstance(GameMode.SOLO, daily = true))

    private fun open(fragment: Fragment) {
        getMainFragmentNavigator?.setMainFragment(fragment) {
            addToBackStack(null)
        }
    }

    private companion object {
        const val KEY_EXPLAINED = "explained"
    }
}
