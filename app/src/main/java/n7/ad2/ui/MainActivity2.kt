package n7.ad2.ui

import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.fragment.app.commit
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import n7.ad2.AD2Logger
import n7.ad2.R
import n7.ad2.data.source.local.AppPreference
import n7.ad2.databinding.ActivityMain2Binding
import n7.ad2.games.GameFragment
import n7.ad2.news.NewsFragment
import n7.ad2.tournaments.TournamentsFragment
import n7.ad2.ui.heroes.HeroesFragment
import n7.ad2.ui.items.ItemsFragment
import n7.ad2.ui.streams.StreamsFragment
import n7.ad2.utils.BaseActivity
import n7.ad2.utils.lazyUnsafe
import javax.inject.Inject

class MainActivity2 : BaseActivity(), DraggableDrawer.Listener {

    private val loggerAdapter: AD2LoggerAdapter by lazyUnsafe { AD2LoggerAdapter() }

    private lateinit var binding: ActivityMain2Binding
    @Inject lateinit var logger: AD2Logger
    @Inject lateinit var preferences: AppPreference

    override fun onCreate(savedInstanceState: Bundle?) {
        (applicationContext as MyApplication).component.inject(this)
        super.onCreate(savedInstanceState)
        binding = ActivityMain2Binding.inflate(layoutInflater)
        setContentView(binding.root)
        setupLoggerAdapter()
        setupMenuAdapter()
        setupInsets()
    }

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        binding.fingerCoordinator.handleGlobalEvent(event)
        return super.dispatchTouchEvent(event)
    }

    override fun setDrawerPercentListener(listener: ((percent: Float) -> Unit)?) {
        binding.draggableDrawer.setDrawerPercentListener(listener)
    }

    private fun setupMenuAdapter() {
        setupLastSelectedMenu()
        val linearLayoutManager = LinearLayoutManager(this)
        val mainMenuAdapter = MainMenuAdapter(layoutInflater, ::setFragment)
        binding.rvMenu.apply {
            overScrollMode = View.OVER_SCROLL_NEVER
            layoutManager = linearLayoutManager
            adapter = mainMenuAdapter
        }
    }

    private fun setupLastSelectedMenu() {
        supportFragmentManager.commit {
            replace(binding.container.id, HeroesFragment.getInstance())
        }
    }

    private fun setFragment(menuItem: MenuItem): Boolean {
        if (!menuItem.isEnable) {
            Snackbar.make(binding.root, getString(R.string.item_disabled), Snackbar.LENGTH_SHORT).show()
            return false
        }
        val fragment = when (menuItem) {
            is HeroesMenuItem -> HeroesFragment.getInstance()
            is GamesMenuItem -> GameFragment()
            is ItemsMenuItem -> ItemsFragment()
            is NewsMenuItem -> NewsFragment()
            is StreamsMenuItem -> StreamsFragment()
            is TournamentsMenuItem -> TournamentsFragment()
        }
        supportFragmentManager.commit {
            replace(binding.container.id, fragment)
        }
        return true
    }

    private fun setupInsets() {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        ViewCompat.setOnApplyWindowInsetsListener(binding.fingerCoordinator) { view, insets ->
            val statusBarsInsets = insets.getInsets(WindowInsetsCompat.Type.statusBars())
            view.updatePadding(top = statusBarsInsets.top)
            insets
        }
    }

    private fun setupLoggerAdapter() = lifecycleScope.launch {
        val shouldDisplayLog = preferences.isShowFingerCoordinate()
        if (!shouldDisplayLog) return@launch
        logger.getLogFlow()
            .onEach(loggerAdapter::add)
            .onEach { binding.rvLog.scrollToPosition(loggerAdapter.itemCount - 1) }
            .launchIn(lifecycleScope)

        binding.rvLog.adapter = loggerAdapter
        val layoutManager = object : LinearLayoutManager(this@MainActivity2) {
            override fun canScrollVertically(): Boolean = false
        }
        layoutManager.stackFromEnd = true
        binding.rvLog.layoutManager = layoutManager
    }

}