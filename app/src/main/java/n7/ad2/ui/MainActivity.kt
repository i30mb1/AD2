package n7.ad2.ui

import android.app.DownloadManager
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.IntentSender.SendIntentException
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.preference.PreferenceManager
import android.view.View
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import androidx.activity.addCallback
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsAnimationCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.view.updatePadding
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.commit
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.transition.ChangeBounds
import androidx.transition.TransitionManager
import androidx.transition.TransitionSet
import com.google.android.material.math.MathUtils.lerp
import com.google.android.material.snackbar.Snackbar
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.InstallState
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import n7.ad2.AD2Logger
import n7.ad2.R
import n7.ad2.databinding.ActivityMainBinding
import n7.ad2.databinding.DialogRateBinding
import n7.ad2.databinding.DialogUpdateBinding
import n7.ad2.databinding.DrawerBinding
import n7.ad2.di.injector
import n7.ad2.games.GameFragment
import n7.ad2.main.MainViewModel
import n7.ad2.news.NewsFragment
import n7.ad2.tournaments.TournamentsFragment
import n7.ad2.ui.heroes.HeroesFragment
import n7.ad2.ui.items.ItemsFragment
import n7.ad2.ui.setting.SettingActivity
import n7.ad2.ui.streams.StreamsFragment
import n7.ad2.utils.BaseActivity
import n7.ad2.utils.lazyUnsafe
import n7.ad2.utils.viewModel
import javax.inject.Inject

class MainActivity : BaseActivity() {

    companion object {
        const val MILLIS_FOR_EXIT = 2000
        const val GITHUB_LAST_APK_URL = "https://github.com/i30mb1/AD2/blob/master/app-release.apk?raw=true"
        const val LOG_ON_RECEIVE = "log"
        const val DIALOG_RATE_SHOW = "DIALOG_RATE_SHOW"
        private const val MY_REQUEST_CODE_UPDATE = 17
    }

    @Inject
    lateinit var preferences: SharedPreferences

    @Inject
    lateinit var logger: AD2Logger

    private var doubleBackToExitPressedOnce = false
    private val constraintSetHidden = ConstraintSet()
    private val constraintSetOrigin = ConstraintSet()
    private var currentSet: ConstraintSet? = null
    private val loggerAdapter: AD2LoggerAdapter by lazyUnsafe { AD2LoggerAdapter() }
    private lateinit var binding: ActivityMainBinding
    private lateinit var drawer: DrawerBinding
    private var shouldUpdateFromMarket = true
    private val viewModel: MainViewModel by viewModel { injector.mainViewModel }
    private var shouldDisplayLog = false
    private var appUpdateManager: AppUpdateManager? = null
    var UpdateListener: InstallStateUpdatedListener = object : InstallStateUpdatedListener {
        override fun onStateUpdate(installState: InstallState) {
            if (installState.installStatus() == InstallStatus.DOWNLOADED) {
                popupSnackbarForCompleteUpdate()
                if (appUpdateManager != null) appUpdateManager!!.unregisterListener(this)
            }
        }
    }
    private var modeSecretActivity = false

    fun startActivityOptions() {
        startActivity(Intent(this, SettingActivity::class.java))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        (applicationContext as MyApplication).component.inject(this)
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        drawer = DataBindingUtil.inflate(layoutInflater, R.layout.drawer, null, false)
        drawer.setViewModel(viewModel)
        drawer.setActivity(this)
        setupLoggerAdapter()
        setupSecretActivity()
        setupMenuRecyclerView()
        setLastFragment()
        setupOnBackPressed()
        setupInsets()
    }

    private fun setupInsets() {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowInsetsControllerCompat(window, binding.root).let { controller ->
//            controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
//            controller.hide(WindowInsetsCompat.Type.systemBars())
            controller.isAppearanceLightStatusBars = false
        }
        ViewCompat.setOnApplyWindowInsetsListener(drawer.root) { view, insets ->
            val navigationBarsInsets = insets.getInsets(WindowInsetsCompat.Type.navigationBars())
            val statusBarsInsets = insets.getInsets(WindowInsetsCompat.Type.statusBars())
            view.updatePadding(bottom = navigationBarsInsets.bottom)
//            binding.root.updateLayoutParams<ViewGroup.MarginLayoutParams> {
//                updateMargins(top = statusBarsInsets.top, bottom = navigationBarsInsets.bottom)
//            }

            val imeVisible = insets.isVisible(WindowInsetsCompat.Type.ime())
            val imeHeight = insets.getInsets(WindowInsetsCompat.Type.ime()).bottom

            WindowInsetsCompat.CONSUMED
        }
        val callback = object : WindowInsetsAnimationCompat.Callback(DISPATCH_MODE_STOP) {

            private var startBottom: Float = 0F
            private var endBottom: Float = 0F

            override fun onPrepare(animation: WindowInsetsAnimationCompat) {
                super.onPrepare(animation)
                startBottom = drawer.root.paddingBottom.toFloat()
            }

            override fun onStart(animation: WindowInsetsAnimationCompat, bounds: WindowInsetsAnimationCompat.BoundsCompat): WindowInsetsAnimationCompat.BoundsCompat {
                endBottom = drawer.root.paddingBottom.toFloat()
                drawer.root.translationY = startBottom - endBottom
                return super.onStart(animation, bounds)
            }

            override fun onProgress(insets: WindowInsetsCompat, runningAnimations: MutableList<WindowInsetsAnimationCompat>): WindowInsetsCompat {
                val offset = lerp(
                    startBottom - endBottom,
                    0F,
                    runningAnimations.first().interpolatedFraction
                )
                drawer.root.translationY = offset
                return insets
            }


            override fun onEnd(animation: WindowInsetsAnimationCompat) {
                super.onEnd(animation)
            }

        }

        ViewCompat.setWindowInsetsAnimationCallback(binding.root, callback)
    }

    private fun setupOnBackPressed() {
        onBackPressedDispatcher.addCallback(this) {
            if (doubleBackToExitPressedOnce) {
                finish()
                return@addCallback
            }
            if (currentSet === constraintSetHidden) {
                toggleSecretActivity(binding.root)
                return@addCallback
            }
            doubleBackToExitPressedOnce = true
            Snackbar.make(binding.root, R.string.main_press_again_to_exit, Snackbar.LENGTH_SHORT).show()
            Handler(Looper.getMainLooper()).postDelayed({ doubleBackToExitPressedOnce = false }, MILLIS_FOR_EXIT.toLong())
        }
    }

    private fun setupMenuRecyclerView() {
        val linearLayoutManager = LinearLayoutManager(this)
        val mainMenuAdapter = MainMenuAdapter(layoutInflater, ::setFragment)
        drawer.rv.apply {
            overScrollMode = View.OVER_SCROLL_NEVER
            layoutManager = linearLayoutManager
            adapter = mainMenuAdapter
        }
    }

    private fun setLastFragment() {
        supportFragmentManager.commit {
            replace(binding.container.id, HeroesFragment())
        }
    }

    private fun setFragment(menu: MenuItem): Boolean {
        if (!menu.isEnable) {
            Snackbar.make(binding.root, getString(R.string.item_disabled), Snackbar.LENGTH_SHORT).show()
            return false
        }
        val fragment = when (menu) {
            is GamesMenuItem -> GameFragment()
            is HeroesMenuItem -> HeroesFragment()
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

    private fun showDialogUpdate() {
        val builder = AlertDialog.Builder(this)
        val binding: DialogUpdateBinding = DataBindingUtil.inflate(layoutInflater, R.layout.dialog_update, null, false)
        builder.setView(binding.root)
        val dialog = builder.create()
        dialog.window!!.attributes.windowAnimations = R.style.MyMaterialAlertDialogTheme
        dialog.show()
        binding.bDialogUpdate.setOnClickListener {
            dialog.dismiss()
            loadNewVersion()
        }
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
    }

    private fun setupLoggerAdapter() {
        shouldDisplayLog = preferences.getBoolean(getString(R.string.setting_log_key), true)
        if (shouldDisplayLog) {

            logger.getLogFlow()
                .onEach(loggerAdapter::add)
                .launchIn(lifecycleScope)

            drawer.rvDrawer.adapter = loggerAdapter
            drawer.rvDrawer.layoutManager = object : LinearLayoutManager(this, VERTICAL, false) {
                override fun canScrollVertically(): Boolean = false
            }
        }
    }

    private fun setupSecretActivity() {
        constraintSetOrigin.clone(drawer.root as ConstraintLayout)
        constraintSetHidden.clone(this, R.layout.drawer_hidden)
        currentSet = constraintSetOrigin
    }

    fun toggleSecretActivity(view: View?): Boolean {
        currentSet = if (currentSet === constraintSetOrigin) constraintSetHidden else constraintSetOrigin
        val transitionSet = TransitionSet()
            .setDuration(500)
            .addTransition(ChangeBounds().setInterpolator(LinearInterpolator()))
        //после этого метода все изменения внутри ViewGroup будут анимированы
        TransitionManager.beginDelayedTransition((drawer.root as ViewGroup), transitionSet)
        //        TransitionManager.beginDelayedTransition((ViewGroup) bindingDrawer.getRoot(), new AutoTransition());
        //применяет все изменения находящиеся в currentSet с анимациями из transitionSet
        currentSet?.applyTo(drawer.root as ConstraintLayout)
        TransitionManager.beginDelayedTransition((binding.root as ViewGroup))
        if (currentSet === constraintSetOrigin) {
            modeSecretActivity = false
            binding.root.visibility = View.VISIBLE
        } else {
            modeSecretActivity = true
            binding.root.visibility = View.INVISIBLE
        }
        return true
    }

    fun loadNewVersion() {
        if (shouldUpdateFromMarket) {
            checkInstallUpdate()
        } else {
            loadNewVersionFromGitHub()
        }
    }

    private fun loadNewVersionFlexible() {
        // Creates instance of the manager.
        if (appUpdateManager == null) appUpdateManager = AppUpdateManagerFactory.create(this)
        // Returns an intent object that you use to check for an update.
        val appUpdateInfoTask = appUpdateManager!!.appUpdateInfo
        // Checks that the platform will allow the specified type of update.
        appUpdateInfoTask.addOnSuccessListener { appUpdateInfo ->
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE // For a flexible update, use AppUpdateType.FLEXIBLE
                && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)
            ) {
                // Request the update.
                try {
                    appUpdateManager!!.startUpdateFlowForResult( // Pass the intent that is returned by 'getAppUpdateInfo()'.
                        appUpdateInfo,  // Or 'AppUpdateType.FLEXIBLE' for flexible updates.
                        AppUpdateType.FLEXIBLE,  // The current activity making the update request.
                        this@MainActivity,  // Include a request code to later monitor this update request.
                        MY_REQUEST_CODE_UPDATE)
                } catch (e: SendIntentException) {
                    loadNewVersionFromMarket()
                }
            }
        }
        appUpdateInfoTask.addOnCompleteListener { }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == MY_REQUEST_CODE_UPDATE) {
            if (resultCode == RESULT_OK) {
                if (appUpdateManager != null) appUpdateManager!!.registerListener(UpdateListener)
            }
        }
    }

    /* Displays the snackbar notification and call to action. */
    private fun popupSnackbarForCompleteUpdate() {
        val snackbar = Snackbar.make(binding.root, R.string.main_activity_update_me, Snackbar.LENGTH_INDEFINITE)
        snackbar.setAction(R.string.main_activity_okay) { if (appUpdateManager != null) appUpdateManager!!.completeUpdate() }
        snackbar.setActionTextColor(resources.getColor(R.color.red_500))
        snackbar.show()
    }

    private fun loadNewVersionFromMarket() {
        try {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$packageName")))
        } catch (a: ActivityNotFoundException) {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=$packageName")))
        }
    }

    private fun loadNewVersionFromGitHub() {
        try {
            val request = DownloadManager.Request(Uri.parse(GITHUB_LAST_APK_URL))
            request.setDescription(getString(R.string.all_new_version))
            request.setTitle(getString(R.string.app_name))
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            request.setMimeType("application/vnd.android.package-archive")
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, getString(R.string.app_name))
            val manager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
            manager.enqueue(request)
        } catch (e: SecurityException) {
            e.printStackTrace()
        }
    }

    private fun showDialogRate() {
        val showDialogRate = PreferenceManager.getDefaultSharedPreferences(this).getBoolean(DIALOG_RATE_SHOW, true)
        if (showDialogRate) {
            val builder = AlertDialog.Builder(this)
            val binding: DialogRateBinding = DataBindingUtil.inflate(layoutInflater, R.layout.dialog_rate, null, false)
            binding.activity = this
            builder.setView(binding.root)
            val dialog = builder.create()
            dialog.window!!.attributes.windowAnimations = R.style.MyMaterialAlertDialogTheme
            dialog.show()
            binding.bDialogRateYes.setOnClickListener {
                dialog.dismiss()
                openAppStore()
            }
            binding.bDialogRateNo.setOnClickListener { dialog.dismiss() }
            PreferenceManager.getDefaultSharedPreferences(this).edit().putBoolean(DIALOG_RATE_SHOW, false).apply()
        }
    }

    fun openAppStore() {
        try {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$packageName")))
        } catch (a: ActivityNotFoundException) {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=$packageName")))
        }
    }

    private fun hideKeyboard() {
        val insetsController = ViewCompat.getWindowInsetsController(drawer.root)
        insetsController?.hide(WindowInsetsCompat.Type.ime())
    }

    private fun checkInstallUpdate() {
        if (appUpdateManager == null) appUpdateManager = AppUpdateManagerFactory.create(this)
        appUpdateManager!!.appUpdateInfo.addOnSuccessListener { appUpdateInfo -> // If the update is downloaded but not installed,
            // notify the user to complete the update.
            when (appUpdateInfo.installStatus()) {
                InstallStatus.DOWNLOADED -> popupSnackbarForCompleteUpdate()
                InstallStatus.UNKNOWN -> loadNewVersionFlexible()
                else -> loadNewVersionFlexible()
            }
        }
    }

}