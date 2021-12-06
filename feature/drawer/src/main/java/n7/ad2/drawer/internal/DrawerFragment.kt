package n7.ad2.drawer.internal

import android.os.Bundle
import android.view.View
import androidx.activity.addCallback
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
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
import kotlinx.coroutines.launch
import n7.ad2.android.DrawerPercentListener
import n7.ad2.android.SplashScreen
import n7.ad2.android.TouchEvent
import n7.ad2.android.extension.lazyUnsafe
import n7.ad2.android.extension.viewModel
import n7.ad2.android.findDependencies
import n7.ad2.drawer.R
import n7.ad2.drawer.databinding.FragmentDrawerBinding
import n7.ad2.drawer.internal.adapter.AD2LoggerAdapter
import n7.ad2.drawer.internal.adapter.MainMenuListAdapter
import n7.ad2.drawer.internal.data.remote.model.VOMenuType
import n7.ad2.drawer.internal.di.DaggerDrawerComponent
import n7.ad2.drawer.internal.domain.vo.VOMenu
import n7.ad2.logger.AD2Logger
import n7.ad2.provider.Provider
import javax.inject.Inject

class DrawerFragment : Fragment(R.layout.fragment_drawer), DrawerPercentListener {

    companion object {
        fun getInstance() = DrawerFragment()
    }

    @Inject lateinit var logger: AD2Logger
    @Inject lateinit var drawerViewModel: DrawerViewModel.Factory
    @Inject lateinit var provider: Provider
//    @Inject lateinit var preferences: AppPreference

    private var _binding: FragmentDrawerBinding? = null
    private val binding: FragmentDrawerBinding get() = _binding!!
    private val loggerAdapter: AD2LoggerAdapter by lazyUnsafe { AD2LoggerAdapter(layoutInflater) }
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

        setupMenuAdapter()
        setupFingerCoordinator()
        setupLoggerAdapter()
        setupInsets()
        setupOnBackPressed()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun setDrawerPercentListener(listener: ((percent: Float) -> Unit)?) {
        binding.draggableDrawer.setDrawerPercentListener(listener)
    }

    private fun setupOnBackPressed() {
        val millisForExit = 2000L
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
                delay(millisForExit)
                doubleBackToExitPressedOnce = false
            }
        }
    }

    private fun setupInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.fingerCoordinator) { view, insets ->
            val statusBarsInsets = insets.getInsets(WindowInsetsCompat.Type.statusBars())
            view.updatePadding(top = statusBarsInsets.top)
            insets
        }
    }

    private fun setFragment(menuItem: VOMenu) {
        val currentTag = childFragmentManager.fragments.lastOrNull()?.tag
        if (currentTag == menuItem.title) return
        val fragment = when (menuItem.type) {
            VOMenuType.HEROES -> provider.heroesApi.getFragment()
            VOMenuType.ITEMS -> provider.itemsApi.getFragment()
            VOMenuType.NEWS -> provider.newsApi.getFragment()
            VOMenuType.TOURNAMENTS -> provider.tournamentsApi.getFragment()
            VOMenuType.STREAMS -> provider.streamApi.getFragment()
            VOMenuType.GAMES -> provider.gamesApi.getFragment()
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
        viewModel.menu.flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
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
//        val shouldDisplayLog = preferences.isShowFingerCoordinate()
//        if (!shouldDisplayLog) return@launch
        logger.getLogFlow()
            .onEach(loggerAdapter::add)
            .onEach { binding.rvLog.scrollToPosition(loggerAdapter.itemCount - 1) }
            .launchIn(lifecycleScope)

        val layoutManager = object : LinearLayoutManager(requireContext()) {
            override fun canScrollVertically(): Boolean = false
        }
        layoutManager.stackFromEnd = true
        binding.rvLog.layoutManager = layoutManager
        binding.rvLog.adapter = loggerAdapter
    }

}