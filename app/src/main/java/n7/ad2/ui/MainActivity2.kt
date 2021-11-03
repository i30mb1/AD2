package n7.ad2.ui

import android.os.Bundle
import android.view.MotionEvent
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.fragment.app.commit
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import n7.ad2.AD2Logger
import n7.ad2.data.source.local.AppPreference
import n7.ad2.databinding.ActivityMain2Binding
import n7.ad2.ui.heroes.HeroesFragment
import n7.ad2.utils.BaseActivity
import n7.ad2.utils.lazyUnsafe
import javax.inject.Inject

class MainActivity2 : BaseActivity() {

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
        setupInsets()
        supportFragmentManager.commit {
            replace(binding.container.id, HeroesFragment())
        }
    }

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        binding.fingerCoordinator.handleGlobalEvent(event)
        return super.dispatchTouchEvent(event)
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