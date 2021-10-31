package n7.ad2.ui

import android.os.Bundle
import android.view.MotionEvent
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

    @Inject
    lateinit var logger: AD2Logger

    @Inject
    lateinit var preferences: AppPreference

    override fun onCreate(savedInstanceState: Bundle?) {
        (applicationContext as MyApplication).component.inject(this)
        super.onCreate(savedInstanceState)
        binding = ActivityMain2Binding.inflate(layoutInflater)
        setContentView(binding.root)
        setupLoggerAdapter()
        supportFragmentManager.commit {
            replace(binding.container.id, HeroesFragment())
        }
    }

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        binding.fingerCoordinator.handleGlobalEvent(event)
        return super.dispatchTouchEvent(event)
    }

    private fun setupLoggerAdapter() = lifecycleScope.launch {
        val shouldDisplayLog = preferences.isShowFingerCoordinate()
        if (shouldDisplayLog) {
            logger.getLogFlow()
                .onEach(loggerAdapter::add)
                .launchIn(lifecycleScope)

            binding.rvLog.adapter = loggerAdapter
            binding.rvLog.layoutManager = object : LinearLayoutManager(this@MainActivity2) {
                override fun canScrollVertically(): Boolean = false
            }
        }
    }

}