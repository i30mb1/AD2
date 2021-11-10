package n7.ad2.ui.main

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
import n7.ad2.AD2Logger
import n7.ad2.R
import n7.ad2.data.source.local.AppPreference
import n7.ad2.data.source.remote.model.VOMenuType
import n7.ad2.databinding.FragmentMainBinding
import n7.ad2.di.injector
import n7.ad2.games.GameFragment
import n7.ad2.news.NewsFragment
import n7.ad2.tournaments.TournamentsFragment
import n7.ad2.ui.MainActivity2
import n7.ad2.ui.heroes.HeroesFragment
import n7.ad2.ui.items.ItemsFragment
import n7.ad2.ui.streams.StreamsFragment
import n7.ad2.ui.vo.VOMenu
import n7.ad2.utils.lazyUnsafe
import n7.ad2.utils.viewModel
import javax.inject.Inject

class MainFragment : Fragment(R.layout.fragment_main), DraggableDrawer.Listener {

    companion object {
        fun getInstance() = MainFragment()
    }

    @Inject lateinit var logger: AD2Logger
    @Inject lateinit var preferences: AppPreference

    private var _binding: FragmentMainBinding? = null
    private val binding: FragmentMainBinding get() = _binding!!
    private val loggerAdapter: AD2LoggerAdapter by lazyUnsafe { AD2LoggerAdapter(layoutInflater) }
    private val viewModel: MainViewModel by viewModel { injector.mainViewModel }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        injector.inject(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentMainBinding.bind(view)

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
        if (!menuItem.isEnable || menuItem.type == VOMenuType.UNKNOWN) {
            Snackbar.make(binding.root, getString(R.string.item_disabled), Snackbar.LENGTH_SHORT).show()
            return
        }
        val currentTag = childFragmentManager.fragments.lastOrNull()?.tag
        if (currentTag == menuItem.title) return
        val fragment = when (menuItem.type) {
            VOMenuType.HEROES -> HeroesFragment.getInstance()
            VOMenuType.ITEMS -> ItemsFragment.getInstance()
            VOMenuType.NEWS -> NewsFragment.getInstance()
            VOMenuType.TOURNAMENTS -> TournamentsFragment.getInstance()
            VOMenuType.STREAMS -> StreamsFragment.getInstance()
            VOMenuType.GAMES -> GameFragment.getInstance()
            VOMenuType.UNKNOWN -> return
        }
        childFragmentManager.commit {
            replace(binding.container.id, fragment, menuItem.title)
        }
        viewModel.updateMenu(menuItem)
    }

    private fun setupMenuAdapter() {
        val linearLayoutManager = LinearLayoutManager(requireContext())
        val mainMenuAdapter = MainMenuAdapter(layoutInflater, viewModel::updateMenu)
        binding.rvMenu.apply {
            layoutManager = linearLayoutManager
            adapter = mainMenuAdapter
        }
        viewModel.menu.flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
            .filter { list -> list.isNotEmpty() }
            .onEach { list ->
                mainMenuAdapter.submitList(list)
                setFragment(list.first { menu -> menu.isSelected })
                (activity as MainActivity2).shouldKeepOnScreen = false
            }
            .launchIn(lifecycleScope)
    }

    private fun setupFingerCoordinator() {
        (activity as TouchEvent).dispatchTouchEvent = { event ->
            binding.fingerCoordinator.handleGlobalEvent(event)
        }
    }

    private fun setupLoggerAdapter() = lifecycleScope.launch {
        val shouldDisplayLog = preferences.isShowFingerCoordinate()
        if (!shouldDisplayLog) return@launch
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