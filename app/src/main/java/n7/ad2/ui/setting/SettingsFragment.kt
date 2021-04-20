package n7.ad2.ui.setting

import android.content.ComponentName
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Build.VERSION
import android.os.Bundle
import androidx.core.app.TaskStackBuilder
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.google.android.play.core.review.ReviewManagerFactory
import n7.ad2.BuildConfig
import n7.ad2.R
import n7.ad2.ui.MainActivity
import n7.ad2.ui.setting.domain.model.Theme

class LauncherRed
class LauncherPurple
class LauncherBlue

class SettingsFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.setting, rootKey)

        setupLog()
        setupNews()
        setupAbout()
        setupContact()
        setupTellFriends()
    }

    private fun setupRateMe() {
        val reviewManager = ReviewManagerFactory.create(requireContext())
        val requestReviewFlow = reviewManager.requestReviewFlow()
        requestReviewFlow.addOnCompleteListener { request ->
            if (request.isSuccessful) {
                val reviewInfo = request.result
                val flow = reviewManager.launchReviewFlow(requireActivity(), reviewInfo)
                flow.addOnCompleteListener {
                    requireActivity().sendBroadcast(Intent(MainActivity.LOG_ON_RECEIVE).putExtra(MainActivity.LOG_ON_RECEIVE, "dialog_rate_is_successful"))
                }
            } else {
                requireActivity().sendBroadcast(Intent(MainActivity.LOG_ON_RECEIVE).putExtra(MainActivity.LOG_ON_RECEIVE, "dialog_rate_is_fail"))
            }
        }
    }

    private fun setupTellFriends() {
        findPreference<Preference>(getString(R.string.setting_tell_friend_key))?.apply {
            val date = sharedPreferences.getInt(getString(R.string.setting_current_day), 0)
            summary = getString(R.string.setting_tell_friend_summary, date)
            onPreferenceClickListener = Preference.OnPreferenceClickListener {
                Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    putExtra(Intent.EXTRA_SUBJECT, getString(R.string.setting_tell_friend_message))
                    putExtra(Intent.EXTRA_TEXT, getString(R.string.setting_tell_friend_message))
                    intent = Intent.createChooser(this, getString(R.string.setting_tell_friend_title))
                    startActivity(this)
                }
                true
            }
        }
    }

    private fun setupContact() {
        findPreference<Preference>(getString(R.string.setting_contact_key))?.apply {
            onPreferenceClickListener = Preference.OnPreferenceClickListener {
                StringBuilder().apply {
                    append("\n\n\n--------------------------\n")
                    append("App Version: ").append(getString(R.string.setting_about_summary, BuildConfig.VERSION_NAME)).append("\n")
                    append("Device Brand: ").append(Build.BRAND).append("\n")
                    append("Device Model: ").append(Build.MODEL).append("\n")
                    append("OS: Android ").append(VERSION.RELEASE).append("(").append(VERSION.CODENAME).append(") \n")

                    val intent = Intent(Intent.ACTION_VIEW)
                    intent.data = Uri.parse("mailto:fate.i30mb1@gmail.com?subject=Feedback about AD2 on Android&body=$it")
                    startActivity(intent)
                }
                true
            }
        }
    }

    private fun setupAbout() {
        findPreference<Preference>(getString(R.string.setting_about_key))?.apply {
            summary = getString(R.string.setting_about_summary, BuildConfig.VERSION_NAME)
            onPreferenceClickListener = Preference.OnPreferenceClickListener {
                startActivity(Intent(activity, LicensesActivity::class.java))
                true
            }
        }
    }

    private fun setupNews() {
        findPreference<Preference>(getString(R.string.setting_news_key))?.apply {
            onPreferenceChangeListener = Preference.OnPreferenceChangeListener { _, _ ->
                recreateActivity()
                true
            }
        }
    }

    private fun setupLog() {
        findPreference<Preference>(getString(R.string.setting_log_key))?.apply {
            onPreferenceChangeListener = Preference.OnPreferenceChangeListener { _, _ ->
                recreateActivity()
                true
            }
        }
    }

    fun applyTheme(theme: Theme) {
        preferenceManager.sharedPreferences.edit().putString(getString(R.string.setting_theme_key), theme.key).apply()
        requireContext().packageManager.apply {
            setComponentEnabledSetting(ComponentName(requireContext(), LauncherRed::class.java), PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP)
            setComponentEnabledSetting(ComponentName(requireContext(), LauncherBlue::class.java), PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP)
            setComponentEnabledSetting(ComponentName(requireContext(), LauncherPurple::class.java), PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP)
            when (theme) {
                Theme.RED -> setComponentEnabledSetting(ComponentName(requireContext(), LauncherRed::class.java), PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP)
                Theme.PURPLE -> setComponentEnabledSetting(ComponentName(requireContext(), LauncherPurple::class.java), PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP)
                Theme.BLUE -> setComponentEnabledSetting(ComponentName(requireContext(), LauncherBlue::class.java), PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP)
            }
        }
        recreateActivity()
    }

    private fun createDialogTheme() {
        val dialogTheme = DialogTheme()
        dialogTheme.show(childFragmentManager, null)
    }

    override fun onDisplayPreferenceDialog(preference: Preference?) {
        when (preference) {
            is DialogThemePreference -> createDialogTheme()
            else -> super.onDisplayPreferenceDialog(preference)
        }

    }

    private fun recreateActivity() {
        TaskStackBuilder.create(requireContext())
            .addNextIntent(Intent(requireActivity(), MainActivity::class.java))
            .addNextIntent(requireActivity().intent)
            .startActivities()
    }
}