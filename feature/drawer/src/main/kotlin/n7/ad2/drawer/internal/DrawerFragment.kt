package n7.ad2.drawer.internal

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.marginBottom
import androidx.core.view.updateLayoutParams
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import n7.ad2.android.DrawerPercentListener
import n7.ad2.android.SplashScreen
import n7.ad2.android.TouchEvent
import n7.ad2.android.findDependencies
import n7.ad2.app.logger.Logger
import n7.ad2.drawer.internal.adapter.LoggerAdapter
import n7.ad2.drawer.internal.adapter.MainMenuListAdapter
import n7.ad2.drawer.internal.data.remote.model.VOMenuType
import n7.ad2.drawer.internal.di.DaggerDrawerComponent
import n7.ad2.drawer.internal.domain.vo.VOMenu
import n7.ad2.feature.drawer.R
import n7.ad2.feature.drawer.databinding.FragmentDrawerBinding
import n7.ad2.ktx.lazyUnsafe
import n7.ad2.ktx.viewModel
import n7.ad2.navigator.Navigator
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds

internal class DrawerFragment : Fragment(R.layout.fragment_drawer), DrawerPercentListener {

    companion object {
        fun getInstance() = DrawerFragment()
    }

    @Inject lateinit var logger: Logger
    @Inject lateinit var drawerViewModel: DrawerViewModel.Factory
    @Inject lateinit var navigator: Navigator

    private var _binding: FragmentDrawerBinding? = null
    private val binding: FragmentDrawerBinding get() = _binding!!
    private val loggerAdapter: LoggerAdapter by lazyUnsafe { LoggerAdapter(layoutInflater) }
    private val viewModel: DrawerViewModel by viewModel { drawerViewModel.create() }
    private val onMenuItemClick: (menuItem: VOMenu) -> Unit = { menuItem: VOMenu ->
        if (!menuItem.isEnable || menuItem.type == VOMenuType.UNKNOWN) {
            Snackbar.make(binding.root, getString(R.string.item_disabled), Snackbar.LENGTH_SHORT).show()
        } else {
            viewModel.updateMenu(menuItem)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DaggerDrawerComponent.factory().create(findDependencies()).inject(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentDrawerBinding.bind(view)
        lifecycle.addObserver(binding.fingerCoordinator)

        setupMenuAdapter()
        setupFingerCoordinator()
        setupLoggerAdapter()
        setupInsets()
        setupOnBackPressed()
        setupSettings()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        lifecycle.removeObserver(binding.fingerCoordinator)
        binding.rvLog.adapter = null
        binding.rvMenu.adapter = null
        _binding = null
        (activity as TouchEvent).dispatchTouchEvent = null
    }

    override fun setDrawerPercentListener(listener: ((percent: Float) -> Unit)?) {
        binding.draggableDrawer.setDrawerPercentListener(listener)
    }

    private fun setupSettings() {
        binding.ivSettings.setOnClickListener { view ->
            val intent = requireContext().packageManager.getLaunchIntentForPackage("by.mev")
            if (intent != null) startActivity(intent)
            view.isSelected = !view.isSelected
        }
    }

    private fun setupOnBackPressed() {
        var doubleBackToExitPressedOnce = false
        activity?.onBackPressedDispatcher?.addCallback(viewLifecycleOwner) {
            val popBack = activity?.supportFragmentManager?.popBackStackImmediate() ?: return@addCallback
            if (popBack) return@addCallback
            if (doubleBackToExitPressedOnce) {
                activity?.finishAffinity()
                return@addCallback
            }
            if (binding.draggableDrawer.isOpen) {
                binding.draggableDrawer.close()
                return@addCallback
            }
            doubleBackToExitPressedOnce = true
            Snackbar.make(binding.root, R.string.main_press_again_to_exit, Snackbar.LENGTH_SHORT).show()
            lifecycleScope.launch {
                delay(2.seconds)
                doubleBackToExitPressedOnce = false
            }
        }
    }

    private fun setupInsets() {
        val ivSettingsMarginBottom = binding.ivSettings.marginBottom
        ViewCompat.setOnApplyWindowInsetsListener(binding.fingerCoordinator) { view, insets ->
            val statusBarsInsets = insets.getInsets(WindowInsetsCompat.Type.statusBars())
            val navigationBarsInsets = insets.getInsets(WindowInsetsCompat.Type.navigationBars())
            view.updatePadding(top = statusBarsInsets.top)
            binding.ivSettings.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                bottomMargin = ivSettingsMarginBottom + navigationBarsInsets.bottom
            }
            insets
        }
    }

    private fun setFragment(menuItem: VOMenu) {
        val currentTag = childFragmentManager.fragments.lastOrNull()?.tag
        if (currentTag == menuItem.title) return
        val fragment = when (menuItem.type) {
            VOMenuType.HEROES -> navigator.heroesApi.getFragment()
            VOMenuType.ITEMS -> navigator.itemsApi.getFragment()
            VOMenuType.NEWS -> navigator.newsApi.getFragment()
            VOMenuType.TOURNAMENTS -> navigator.tournamentsApi.getFragment()
            VOMenuType.STREAMS -> navigator.streamApi.getFragment()
            VOMenuType.GAMES -> navigator.gamesApi.getFragment()
            VOMenuType.UNKNOWN -> return
        }
        childFragmentManager.commit {
            replace(binding.container.id, fragment, menuItem.title)
        }
    }

    private fun setupMenuAdapter() {
        val linearLayoutManager = LinearLayoutManager(requireContext())
        val mainMenuAdapter = MainMenuListAdapter(layoutInflater, onMenuItemClick)
        binding.rvMenu.apply {
            layoutManager = linearLayoutManager
            adapter = mainMenuAdapter
        }
        viewModel.menu.flowWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.State.STARTED)
            .filter { list -> list.isNotEmpty() }
            .onEach { list ->
                mainMenuAdapter.submitList(list)
                setFragment(list.first { menu -> menu.isSelected })
                (activity as SplashScreen).shouldKeepOnScreen = false
            }
            .launchIn(lifecycleScope)
    }

    private fun setupFingerCoordinator() {
        (activity as TouchEvent).dispatchTouchEvent = { event ->
            binding.fingerCoordinator.handleGlobalEvent(event)
        }
    }

    private fun setupLoggerAdapter() = lifecycleScope.launch {
        val shouldDisplayLog = viewModel.isLogWidgetEnabled()
        if (!shouldDisplayLog) return@launch
        val logLayoutManager = object : LinearLayoutManager(requireContext()) {
            override fun canScrollVertically(): Boolean = false
        }
        logLayoutManager.stackFromEnd = true
        binding.rvLog.layoutManager = logLayoutManager
        binding.rvLog.adapter = loggerAdapter
        logger.getLogFlow()
            .flowWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.State.CREATED)
            .onStart { loggerAdapter.clear() }
            .onEach(loggerAdapter::add)
            .onEach { binding.rvLog.scrollToPosition(loggerAdapter.itemCount - 1) }
            .launchIn(lifecycleScope)
    }

}