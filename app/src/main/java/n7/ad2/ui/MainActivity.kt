package n7.ad2.ui

import android.app.DownloadManager
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.IntentSender.SendIntentException
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.View
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.view.updatePadding
import androidx.fragment.app.FragmentActivity
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
import n7.ad2.R
import n7.ad2.android.extension.lazyUnsafe
import n7.ad2.databinding.ActivityMainBinding

class MainActivity : FragmentActivity() {

    companion object {
        const val GITHUB_LAST_APK_URL = "https://github.com/i30mb1/AD2/blob/master/app-release.apk?raw=true"
        const val LOG_ON_RECEIVE = "log"
        const val DIALOG_RATE_SHOW = "DIALOG_RATE_SHOW"
        private const val MY_REQUEST_CODE_UPDATE = 17
    }

    private val constraintSetHidden = ConstraintSet()
    private val constraintSetOrigin = ConstraintSet()
    private var currentSet: ConstraintSet? = null
    private val loggerAdapter: n7.ad2.drawer.internal.adapter.AD2LoggerAdapter by lazyUnsafe { n7.ad2.drawer.internal.adapter.AD2LoggerAdapter(layoutInflater) }
    private lateinit var binding: ActivityMainBinding
    private var shouldUpdateFromMarket = true
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
        (applicationContext as MyApplication).component.inject(this)
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupInsets()
    }

    private fun setupInsets() {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowInsetsControllerCompat(window, binding.root).let { controller ->
//            controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
//            controller.hide(WindowInsetsCompat.Type.systemBars())
            controller.isAppearanceLightStatusBars = false
        }
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, insets ->
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
    }

    fun toggleSecretActivity(view: View?): Boolean {
        currentSet = if (currentSet === constraintSetOrigin) constraintSetHidden else constraintSetOrigin
        val transitionSet = TransitionSet()
            .setDuration(500)
            .addTransition(ChangeBounds().setInterpolator(LinearInterpolator()))
        //после этого метода все изменения внутри ViewGroup будут анимированы
//        TransitionManager.beginDelayedTransition((drawer.root as ViewGroup), transitionSet)
        //        TransitionManager.beginDelayedTransition((ViewGroup) bindingDrawer.getRoot(), new AutoTransition());
        //применяет все изменения находящиеся в currentSet с анимациями из transitionSet
//        currentSet?.applyTo(drawer.root as ConstraintLayout)
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


    fun openAppStore() {
        try {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$packageName")))
        } catch (a: ActivityNotFoundException) {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=$packageName")))
        }
    }

    private fun hideKeyboard() {
        val insetsController = ViewCompat.getWindowInsetsController(binding.root)
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