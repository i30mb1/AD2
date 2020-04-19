package n7.ad2.ui.setting

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.util.AttributeSet
import android.view.View
import androidx.fragment.app.DialogFragment
import androidx.preference.EditTextPreference
import androidx.preference.EditTextPreferenceDialogFragmentCompat
import androidx.preference.PreferenceDialogFragmentCompat
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import n7.ad2.R
import n7.ad2.databinding.DialogSettingFragmentThemeBinding
import n7.ad2.ui.setting.SettingsFragment.Companion.THEME_DARK
import n7.ad2.ui.setting.SettingsFragment.Companion.THEME_GRAY
import n7.ad2.ui.setting.SettingsFragment.Companion.THEME_WHITE

class DialogTheme : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val settingsFragment = parentFragment as SettingsFragment

        val dialogView = DialogSettingFragmentThemeBinding.inflate(layoutInflater).apply {
            bDark.setOnClickListener { settingsFragment.applyTheme(THEME_DARK) }
            bGray.setOnClickListener { settingsFragment.applyTheme(THEME_GRAY) }
            bWhite.setOnClickListener { settingsFragment.applyTheme(THEME_WHITE) }
        }

        val dialogBuilder = MaterialAlertDialogBuilder(context)
                .setView(dialogView.root)

        return dialogBuilder.create()
    }

}

class DialogThemePreference(context: Context, attrs: AttributeSet) : EditTextPreference(context, attrs) {

    override fun getDialogLayoutResource(): Int {
        return R.layout.dialog_setting_fragment_theme
    }

}

class DialogNew : PreferenceDialogFragmentCompat() {

    fun newInstance(key: String?): PreferenceDialogFragmentCompat {
        val fragment = DialogNew()
        val b = Bundle(1)
        b.putString(ARG_KEY, key)
        fragment.arguments = b
        return fragment
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        preference.dialogTitle = null
        preference.negativeButtonText = null
        preference.positiveButtonText = null
        super.onCreate(savedInstanceState)
    }

    override fun onBindDialogView(view: View?) {
        super.onBindDialogView(view)

    }

    override fun onDialogClosed(positiveResult: Boolean) {

    }

}
