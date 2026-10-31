package n7.ad2.games.internal

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.launch
import n7.ad2.android.DependenciesMap
import n7.ad2.android.DrawerPercentListener
import n7.ad2.android.HasDependencies
import n7.ad2.android.findDependencies
import n7.ad2.android.getMainFragmentNavigator
import n7.ad2.app.logger.Logger
import n7.ad2.core.ui.content
import n7.ad2.games.internal.compose.GamesScreen
import n7.ad2.games.internal.data.GameVO
import n7.ad2.games.internal.di.DaggerGamesComponent
import n7.ad2.games.internal.games.killCreep.KillCreepFragment
import n7.ad2.games.internal.games.killCreep.KillCreepViewModel
import n7.ad2.games.internal.games.skillmp.SkillGameFragment
import n7.ad2.games.internal.leaderboard.LeaderboardFragment
import n7.ad2.games.room.GameMode
import n7.ad2.ktx.viewModel
import n7.ad2.xo.api.XoProvider
import javax.inject.Inject

internal class GamesFragment @JvmOverloads constructor(override var dependenciesMap: DependenciesMap = emptyMap()) :
    Fragment(),
    HasDependencies {

    @Inject lateinit var gamesViewModelFactory: GamesViewModel.Factory

    @Inject lateinit var logger: Logger

    private val viewModel: GamesViewModel by viewModel { gamesViewModelFactory.create() }

    // Wi-Fi Direct: с Android 13 отдельное разрешение без геолокации, раньше - точная геолокация
    private val wifiDirectPermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) Manifest.permission.NEARBY_WIFI_DEVICES else Manifest.permission.ACCESS_FINE_LOCATION
    private val permissionRequest = registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted -> viewModel.onWifiDirectPermission(granted) }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        DaggerGamesComponent.factory().create(findDependencies()).inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View = content {
        GamesScreen(viewModel, parentFragment as? DrawerPercentListener, ::onGameClicked, ::onLeaderboardClicked)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (ContextCompat.checkSelfPermission(requireContext(), wifiDirectPermission) == PackageManager.PERMISSION_GRANTED) {
            viewModel.onWifiDirectPermission(true)
        } else if (savedInstanceState == null) {
            permissionRequest.launch(wifiDirectPermission)
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.openGame.collect { game -> open(gameFragment(game.gameId, game.mode)) }
            }
        }
    }

    // виден только список: в игре и в рекордах телефон не объявляется (сессия остается)
    override fun onStart() {
        super.onStart()
        viewModel.setVisible(true)
    }

    override fun onStop() {
        viewModel.setVisible(false)
        super.onStop()
    }

    /** Соло открывается сразу, вдвоем - это выбор в сессии: игра откроется, когда второй выберет то же */
    private fun onGameClicked(game: GameVO, mode: GameMode) {
        if (game is GameVO.XO) return open(XoProvider().getFragment().getDeclaredConstructor().newInstance())
        val gameId = game.quizId ?: return
        if (mode == GameMode.SOLO) open(gameFragment(gameId, mode)) else viewModel.pick(gameId, mode)
    }

    private fun gameFragment(gameId: String, mode: GameMode): Fragment =
        if (gameId == KillCreepViewModel.GAME_ID) KillCreepFragment.getInstance(mode) else SkillGameFragment.getInstance(mode, gameId)

    private fun onLeaderboardClicked() = open(LeaderboardFragment())

    private fun open(fragment: Fragment) {
        getMainFragmentNavigator?.setMainFragment(fragment) {
            addToBackStack(null)
        }
    }
}
