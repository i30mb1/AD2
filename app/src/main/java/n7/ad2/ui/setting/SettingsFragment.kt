package n7.ad2.ui.setting

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Build.VERSION
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.core.app.TaskStackBuilder
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import n7.ad2.BuildConfig
import n7.ad2.R
import n7.ad2.main.MainActivity

class SettingsFragment : PreferenceFragmentCompat() {

    companion object {
        const val THEME_GRAY = "GRAY"
        const val THEME_WHITE = "WHITE"
        const val THEME_DARK = "DARK"
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.setting, rootKey)

        setupTheme()
        setupLog()
        setupNews()
        setupAbout()
        setupContact()
        setupTellFriends()
    }

    private fun setupTellFriends() {
        findPreference<Preference>(getString(R.string.setting_tell_friend_key))?.apply {
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

    private fun setupTheme() {
        findPreference<Preference>(getString(R.string.setting_theme_key))?.apply {
            onPreferenceClickListener = Preference.OnPreferenceClickListener {
                createDialogTheme(it)
                true
            }
        }
    }

    private fun createDialogTheme(preference: Preference) {
        val dialogTheme = DialogTheme()
        dialogTheme.show(childFragmentManager, null)

//        val builder = AlertDialog.Builder(requireContext())
//        builder.setView(R.layout.dialog_setting_fragment_theme)
//        builder.create().apply {
//            window?.attributes?.windowAnimations = R.style.DialogTheme

//            findViewById<View>(R.id.b_dialog_theme_dark)?.setOnClickListener {
//                preference.sharedPreferences.edit().putString(preference.key, THEME_DARK).apply()
//                recreateActivity()
//            }
//            findViewById<View>(R.id.b_dialog_theme_gray)?.setOnClickListener {
//                preference.sharedPreferences.edit().putString(preference.key, THEME_GRAY).apply()
//                recreateActivity()
//            }
//            findViewById<View>(R.id.b_dialog_theme_white)?.setOnClickListener {
//                preference.sharedPreferences.edit().putString(preference.key, THEME_WHITE).apply()
//                recreateActivity()
//            }

//            show()
//        }
    }

    private fun recreateActivity() {
        TaskStackBuilder.create(requireContext())
                .addNextIntent(Intent(requireActivity(), MainActivity::class.java))
                .addNextIntent(requireActivity().intent)
                .startActivities()
    }
}