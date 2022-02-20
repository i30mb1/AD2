package n7.ad2.ui.setting

import android.app.Activity
import android.content.ComponentName
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Build.VERSION
import androidx.core.app.TaskStackBuilder
import n7.ad2.BuildConfig
import n7.ad2.R
import n7.ad2.ui.MainActivity
import n7.ad2.ui.setting.domain.model.Theme

class SettingsFragment : Activity() {

    private fun setupRateMe() {
//        val reviewManager = ReviewManagerFactory.create(requireContext())
//        val requestReviewFlow = reviewManager.requestReviewFlow()
//        requestReviewFlow.addOnCompleteListener { request ->
//            if (request.isSuccessful) {
//                val reviewInfo = request.result
//                val flow = reviewManager.launchReviewFlow(requireActivity(), reviewInfo)
//                flow.addOnCompleteListener {
//                    requireActivity().sendBroadcast(Intent(MainActivity.LOG_ON_RECEIVE).putExtra(MainActivity.LOG_ON_RECEIVE, "dialog_rate_is_successful"))
//                }
//            } else {
//                requireActivity().sendBroadcast(Intent(MainActivity.LOG_ON_RECEIVE).putExtra(MainActivity.LOG_ON_RECEIVE, "dialog_rate_is_fail"))
//            }
//        }
    }

    private fun setupTellFriends() {
        Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, getString(R.string.setting_tell_friend_message))
            putExtra(Intent.EXTRA_TEXT, getString(R.string.setting_tell_friend_message))
            intent = Intent.createChooser(this, getString(R.string.setting_tell_friend_title))
            startActivity(this)
        }
    }

    private fun setupContact() {
        StringBuilder().apply {
            append("\n\n\n--------------------------\n")
            append("App Version: ").append(getString(R.string.setting_about_summary, BuildConfig.VERSION_NAME)).append("\n")
            append("Device Brand: ").append(Build.BRAND).append("\n")
            append("Device Model: ").append(Build.MODEL).append("\n")
            append("OS: Android ").append(VERSION.RELEASE).append("(").append(VERSION.CODENAME).append(") \n")

            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse("mailto:fate.i30mb1@gmail.com?subject=Feedback about AD2 on Android&body=hehe")
            startActivity(intent)
        }
    }


    fun applyTheme(theme: Theme) {
        Theme.values()
            .forEach {
                val newState = if (it.key == theme.key) PackageManager.COMPONENT_ENABLED_STATE_ENABLED else PackageManager.COMPONENT_ENABLED_STATE_DISABLED
                packageManager.setComponentEnabledSetting(ComponentName(this, it.componentClass), newState, PackageManager.DONT_KILL_APP)
            }
        recreateActivity()
    }



    private fun recreateActivity() {
        TaskStackBuilder.create(this)
            .addNextIntent(Intent(this, MainActivity::class.java))
            .addNextIntent(intent)
            .startActivities()
    }
}