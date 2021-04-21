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
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.databinding.DataBindingUtil
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableInt
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.transition.ChangeBounds
import androidx.transition.TransitionManager
import androidx.transition.TransitionSet
import com.google.android.material.snackbar.Snackbar
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.InstallState
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability
import com.yarolegovich.slidingrootnav.SlidingRootNavBuilder
import com.yarolegovich.slidingrootnav.callback.DragStateListener
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
import n7.ad2.utils.viewModel
import javax.inject.Inject

class MainActivity : BaseActivity() {

    companion object {
        const val LAST_ITEM = "LAST_ITEM"
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

    var observableLastItem = ObservableInt(1)
    var subscription = ObservableBoolean(false)

    private var timeCounter = -1
    private var doubleBackToExitPressedOnce = false
    private val constraintSetHidden = ConstraintSet()
    private val constraintSetOrigin = ConstraintSet()
    private var currentSet: ConstraintSet? = null
    private val loggerAdapter: AD2LoggerAdapter by lazy { AD2LoggerAdapter() }
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
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        drawer = DataBindingUtil.inflate(layoutInflater, R.layout.drawer, null, false)
        drawer.setViewModel(viewModel)
        drawer.setActivity(this)
        setupLoggerAdapter()
        setupToolbar()
        setupDrawer()
        setupSecretActivity()
        setLastFragment()
        setupMenuRecyclerView()
    }

    private fun setupMenuRecyclerView() {
        val linearLayoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        val mainMenuAdapter = MainMenuAdapter(layoutInflater) { _ ->

        }
        drawer.rv.apply {
            overScrollMode = View.OVER_SCROLL_NEVER
            layoutManager = linearLayoutManager
            adapter = mainMenuAdapter
        }
    }

    fun setLastFragment() {
        val lastItem = PreferenceManager.getDefaultSharedPreferences(this).getInt(LAST_ITEM, 1)
        observableLastItem.set(lastItem)
        setFragment(lastItem, false)
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
                .flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
                .onEach(loggerAdapter::add)
                .launchIn(lifecycleScope)

            drawer.rvDrawer.adapter = loggerAdapter
            drawer.rvDrawer.layoutManager = object : LinearLayoutManager(this, VERTICAL, false) {
                override fun canScrollVertically(): Boolean = false
            }
        }
    }

    fun setFragment(fragmentID: Int, closeDrawer: Boolean) {
        observableLastItem.set(fragmentID)
        val ft = supportFragmentManager.beginTransaction()
        when (fragmentID) {
            1 -> ft.replace(binding.container.id, HeroesFragment()).commit()
            2 -> ft.replace(binding.container.id, ItemsFragment()).commit()
            3 -> ft.replace(binding.container.id, NewsFragment()).commit()
            4 -> ft.replace(binding.container.id, TournamentsFragment()).commit()
            5 -> ft.replace(binding.container.id, StreamsFragment()).commit()
            6 -> ft.replace(binding.container.id, GameFragment()).commit()
            else -> ft.replace(binding.container.id, HeroesFragment()).commit()
        }
        //        if (closeDrawer)
//            new Handler().postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    drawer.closeMenu();
//                }
//            }, 50);
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
        TransitionManager.beginDelayedTransition((drawer!!.root as ViewGroup), transitionSet)
        //        TransitionManager.beginDelayedTransition((ViewGroup) bindingDrawer.getRoot(), new AutoTransition());
        //применяет все изменения находящиеся в currentSet с анимациями из transitionSet
        currentSet!!.applyTo(drawer!!.root as ConstraintLayout)
        TransitionManager.beginDelayedTransition((binding!!.root as ViewGroup))
        if (currentSet === constraintSetOrigin) {
            modeSecretActivity = false
            binding!!.root.visibility = View.VISIBLE
        } else {
            modeSecretActivity = true
            binding!!.root.visibility = View.INVISIBLE
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

    private fun incCountEnter() {
        timeCounter++
        //        if (timeCounter > COUNTER_DIALOG_RATE) showDialogRate();
//        if (timeCounter > COUNTER_DIALOG_DONATE) showPreDialogDonate();
//        if (timeCounter % ACTION_BEFORE_SHOW_ADVERTISEMENT == 0) ShowInterstitialAd();
    }

    private fun showDialogRate() {
        val showDialogRate = PreferenceManager.getDefaultSharedPreferences(this).getBoolean(DIALOG_RATE_SHOW, true)
        if (showDialogRate && !subscription.get()) {
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
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm?.hideSoftInputFromWindow(binding!!.toolbar.windowToken, 0)
    }

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        drawer.fingerCoordinator.handleGlobalEvent(event)
        return super.dispatchTouchEvent(event)
    }

    private fun setupDrawer() {
        val drawer = SlidingRootNavBuilder(this)
            .withToolbarMenuToggle(binding!!.toolbar)
            .withDragDistance(110)
            .withRootViewScale(0.65f)
            .withRootViewElevation(8)
            .withRootViewYTranslation(0)
            .withContentClickableWhenMenuOpened(true)
            .addDragStateListener(object : DragStateListener {
                override fun onDragStart() {
                    hideKeyboard()
                }

                override fun onDragEnd(isMenuOpened: Boolean) {}
            })
            .withMenuView(drawer!!.root)
            .inject()
        drawer.openMenu()
    }

    override fun onStop() {
        PreferenceManager.getDefaultSharedPreferences(this).edit().putInt(LAST_ITEM, observableLastItem.get()).apply()
        super.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onResume() {
        incCountEnter()
        //        checkInstallUpdate();
        super.onResume()
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

    override fun onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            finish()
            return
        }
        if (currentSet === constraintSetHidden) {
            toggleSecretActivity(binding.root)
            return
        }
        doubleBackToExitPressedOnce = true
        Snackbar.make(binding.root, R.string.main_press_again_to_exit, Snackbar.LENGTH_SHORT).show()
        Handler(Looper.getMainLooper()).postDelayed({ doubleBackToExitPressedOnce = false }, MILLIS_FOR_EXIT.toLong())
    }

}