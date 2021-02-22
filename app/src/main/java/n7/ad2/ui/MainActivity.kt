package n7.ad2.ui

import android.app.DownloadManager
import n7.ad2.utils.BaseActivity
import androidx.databinding.ObservableInt
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableArrayList
import androidx.constraintlayout.widget.ConstraintSet
import n7.ad2.utils.PlainAdapter
import android.content.Intent
import n7.ad2.ui.MainActivity
import n7.ad2.main.MainViewModel
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.InstallState
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import n7.ad2.R
import androidx.lifecycle.ViewModelProvider
import androidx.annotation.StringRes
import n7.ad2.utils.SnackbarUtils
import android.content.DialogInterface
import n7.ad2.utils.UnscrollableLinearLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import n7.ad2.ui.heroes.HeroesFragment
import n7.ad2.ui.items.ItemsFragment
import n7.ad2.news.NewsFragment
import n7.ad2.tournaments.TournamentsFragment
import n7.ad2.ui.streams.StreamsFragment
import n7.ad2.games.GameFragment
import androidx.constraintlayout.widget.ConstraintLayout
import android.view.animation.LinearInterpolator
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.install.model.UpdateAvailability
import com.google.android.play.core.install.model.AppUpdateType
import android.content.IntentSender.SendIntentException
import com.google.android.material.snackbar.Snackbar
import android.content.ActivityNotFoundException
import android.content.BroadcastReceiver
import android.content.Context
import com.google.firebase.analytics.FirebaseAnalytics
import android.view.MotionEvent
import com.yarolegovich.slidingrootnav.SlidingRootNav
import com.yarolegovich.slidingrootnav.SlidingRootNavBuilder
import com.yarolegovich.slidingrootnav.callback.DragStateListener
import android.content.IntentFilter
import android.net.Uri
import android.os.Environment
import android.os.Handler
import android.preference.PreferenceManager
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import androidx.transition.ChangeBounds
import androidx.transition.TransitionManager
import androidx.transition.TransitionSet
import com.google.android.play.core.install.model.InstallStatus
import n7.ad2.databinding.ActivityMainBinding
import n7.ad2.databinding.DialogRateBinding
import n7.ad2.databinding.DialogUpdateBinding
import n7.ad2.databinding.DrawerBinding
import java.util.Arrays

class MainActivity : BaseActivity() {
    var observableLastItem = ObservableInt(1)
    var rewardedVideoLoaded = ObservableBoolean(false)
    var freeSubscriptionCounter = ObservableInt(0)
    var subscription = ObservableBoolean(false)
    var movementListX = ObservableArrayList<Float>()
    var movementListY = ObservableArrayList<Float>()
    private val easter_egg_value = false
    private var timeCounter = -1
    private var doubleBackToExitPressedOnce = false
    private val constraintSetHidden = ConstraintSet()
    private val constraintSetOrigin = ConstraintSet()
    private var currentSet: ConstraintSet? = null
    private var adapter: PlainAdapter? = null
    private lateinit var binding: ActivityMainBinding
    private lateinit var drawer: DrawerBinding
    var broadcastReceiver: BroadcastReceiver? = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val string = intent.getStringExtra(LOG_ON_RECEIVE)
            log(string)
        }
    }
    private var shouldUpdateFromMarket = true
    private var viewModel: MainViewModel? = null
    private var currentDay = 0
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
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        currentDay = PreferenceManager.getDefaultSharedPreferences(this).getInt(getString(R.string.setting_current_day), 0)
        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        binding.setViewModel(viewModel)
        drawer = DataBindingUtil.inflate(layoutInflater, R.layout.drawer, null, false)
        drawer.setViewModel(viewModel)
        movementListX.addAll(Arrays.asList(*arrayOfNulls(10)))
        movementListY.addAll(Arrays.asList(*arrayOfNulls(10)))
        drawer.setArrayX(movementListX)
        drawer.setArrayY(movementListY)
        drawer.setActivity(this)
        setupRecyclerView()
        log("on_Create")
        setupToolbar()
        setupDrawer()
        setupListeners()
        setupSecretActivity()
        setLastFragment()
    }

    private fun setupListeners() {
        viewModel!!.snackbarMessage.observe(this, Observer { redId -> SnackbarUtils.showSnackbar(binding!!.root, getString(redId!!)) })
        viewModel!!.showDialogUpdate.observe(this, {
            //                showDialogUpdate();
        })
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
        shouldUpdateFromMarket = PreferenceManager.getDefaultSharedPreferences(this).getBoolean(MainViewModel.SHOULD_UPDATE_FROM_MARKET, true)
        dialog.setOnCancelListener { PreferenceManager.getDefaultSharedPreferences(this@MainActivity).edit().putInt(MainViewModel.LAST_DAY_WHEN_CHECK_UPDATE, currentDay).apply() }
    }

    private fun setupToolbar() {
        setSupportActionBar(binding!!.toolbar)
    }

    override fun onStart() {
        super.onStart()
        log("on_Start")
    }

    private fun setupRecyclerView() {
        shouldDisplayLog = PreferenceManager.getDefaultSharedPreferences(this).getBoolean(getString(R.string.setting_log_key), true)
        if (shouldDisplayLog) {
            adapter = viewModel!!.adapter
            drawer!!.rvDrawer.adapter = adapter
            drawer!!.rvDrawer.layoutManager = UnscrollableLinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        }
    }

    fun log(text: String?) {
        if (adapter != null) {
            adapter!!.add(text)
            adapter!!.notifyDataSetChanged()
            //            adapter.notifyItemChanged(adapter.getItemCount());
            drawer!!.rvDrawer.scrollToPosition(adapter!!.itemCount - 1)
        }
    }

    fun setFragment(fragmentID: Int, closeDrawer: Boolean) {
        observableLastItem.set(fragmentID)
        val ft = supportFragmentManager.beginTransaction()
        when (fragmentID) {
            1 -> ft.replace(binding!!.container.id, HeroesFragment()).commit()
            2 -> ft.replace(binding!!.container.id, ItemsFragment()).commit()
            3 -> ft.replace(binding!!.container.id, NewsFragment()).commit()
            4 -> ft.replace(binding!!.container.id, TournamentsFragment()).commit()
            5 -> ft.replace(binding!!.container.id, StreamsFragment()).commit()
            6 -> ft.replace(binding!!.container.id, GameFragment()).commit()
            else -> ft.replace(binding!!.container.id, HeroesFragment()).commit()
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
        constraintSetOrigin.clone(drawer!!.root as ConstraintLayout)
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
                    log("load_version_flexible = failed")
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
            } else {
                log("Update flow failed! Result code: $resultCode")
            }
        }
    }

    /* Displays the snackbar notification and call to action. */
    private fun popupSnackbarForCompleteUpdate() {
        val snackbar = Snackbar.make(binding!!.root, R.string.main_activity_update_me, Snackbar.LENGTH_INDEFINITE)
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
            log("Loading latest version from GitHub")
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

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        if (!shouldDisplayLog) return super.dispatchTouchEvent(ev)
        // событие
        val action = ev.actionMasked
        // индекс косания
        var index = ev.actionIndex
        // id косания
        var id = ev.getPointerId(index)
        when (action) {
            MotionEvent.ACTION_DOWN, MotionEvent.ACTION_POINTER_DOWN -> {
                movementListX[id] = ev.getX(index)
                movementListY[id] = ev.getY(index)
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_POINTER_UP, MotionEvent.ACTION_CANCEL -> {
                movementListX[id] = 0f
                movementListY[id] = 0f
            }
            MotionEvent.ACTION_MOVE -> {
                // число косаний
                val count = ev.pointerCount
                //                log("fingers = " +count);
                var i = 0
                while (i < count) {
                    index = i
                    id = ev.getPointerId(index)
                    movementListX[id] = ev.getX(index)
                    movementListY[id] = ev.getY(index)
                    i++
                }
                if (count > 9) {
//                    showDialogCongratulations();
                }
            }
        }
        drawer!!.arrayX = movementListX
        drawer!!.arrayY = movementListY
        return super.dispatchTouchEvent(ev)
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

    override fun onPause() {
        log("on_Pause")
        super.onPause()
    }

    override fun onStop() {
        log("on_Stop")
        PreferenceManager.getDefaultSharedPreferences(this).edit().putInt(LAST_ITEM, observableLastItem.get()).apply()
        super.onStop()
    }

    override fun onDestroy() {
        if (broadcastReceiver != null) unregisterReceiver(broadcastReceiver)
        log("on_Destroy")
        super.onDestroy()
    }

    override fun onResume() {
        log("on_Resume")
        incCountEnter()
        regReceiverLog()
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

    private fun regReceiverLog() {
        val filter = IntentFilter()
        filter.addAction(LOG_ON_RECEIVE)
        registerReceiver(broadcastReceiver, filter)
        //можно затригерить ресивер этой командой
//        getActivity().sendBroadcast(new Intent("setToolbarName"));
    }

    override fun onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            finish()
            return
        }
        if (currentSet === constraintSetHidden) {
            toggleSecretActivity(binding!!.root)
            return
        }
        doubleBackToExitPressedOnce = true
        Snackbar.make(binding!!.root, R.string.main_press_again_to_exit, Snackbar.LENGTH_SHORT).show()
        Handler().postDelayed({ doubleBackToExitPressedOnce = false }, MILLIS_FOR_EXIT.toLong())
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
    }

    companion object {
        const val COUNTER_DIALOG_RATE = 10
        const val COUNTER_DIALOG_DONATE = 15
        const val FREE_SUBSCRIPTION_COUNTER = "FREE_SUBSCRIPTION_COUNTER"
        const val FIREBASE_DIALOG_DONATE_SAW = "DIALOG_DONATE_SAW"
        const val FIREBASE_DIALOG_PRE_DONATE_SAW = "DIALOG_PRE_DONATE_SAW"
        const val FIREBASE_DIALOG_RATE_SAW = "DIALOG_RATE_SHOW"
        const val FIREBASE_DIALOG_RATE_CLICK = "DIALOG_RATE_CLICK"
        const val LAST_ITEM = "LAST_ITEM"
        const val MILLIS_FOR_EXIT = 2000
        const val GITHUB_LAST_APK_URL = "https://github.com/i30mb1/AD2/blob/master/app-release.apk?raw=true"
        const val ADMOB_ID = "ca-app-pub-5742225922710304/8697652489"
        const val ADMOB_ID_FAKE = "ca-app-pub-3940256099942544/5224354917"
        const val ADMOB_ID_BACK = "ca-app-pub-5742225922710304/6328775876"
        const val ADMOB_ID_BACK_FAKE = "ca-app-pub-3940256099942544/1033173712"
        const val LOG_ON_RECEIVE = "log"
        const val DIALOG_RATE_SHOW = "DIALOG_RATE_SHOW"
        const val DIALOG_VIDEO_AD_SAW = "DIALOG_VIDEO_AD_SAW"
        const val ACTION_BEFORE_SHOW_ADVERTISEMENT = 3
        const val EASTER_EGG_ACTIVATED = "EASTER_EGG_ACTIVATED"
        private const val DIALOG_PRE_DONATE_LAST_DAY = "DIALOG_PRE_DONATE_LAST_DAY"
        private const val MY_REQUEST_CODE_UPDATE = 17
    }
}