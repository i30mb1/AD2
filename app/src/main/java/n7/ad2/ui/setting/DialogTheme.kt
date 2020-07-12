package n7.ad2.ui.setting

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.util.AttributeSet
import androidx.fragment.app.DialogFragment
import androidx.preference.EditTextPreference
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import n7.ad2.databinding.DialogThemeBinding
import n7.ad2.ui.setting.SettingsFragment.Companion.THEME_DARK
import n7.ad2.ui.setting.SettingsFragment.Companion.THEME_GRAY
import n7.ad2.ui.setting.SettingsFragment.Companion.THEME_WHITE

class DialogTheme : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val settingsFragment = parentFragment as SettingsFragment

        val dialogView = DialogThemeBinding.inflate(layoutInflater).apply {
            bDark.setOnClickListener { settingsFragment.applyTheme(THEME_DARK) }
            bGray.setOnClickListener { settingsFragment.applyTheme(THEME_GRAY) }
            bWhite.setOnClickListener { settingsFragment.applyTheme(THEME_WHITE) }
        }

        val dialogBuilder = MaterialAlertDialogBuilder(requireContext())
                .setView(dialogView.root)

        return dialogBuilder.create()
    }

}

class DialogThemePreference(context: Context, attrs: AttributeSet) : EditTextPreference(context, attrs)

