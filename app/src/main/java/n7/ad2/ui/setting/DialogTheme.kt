package n7.ad2.ui.setting

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.util.AttributeSet
import androidx.fragment.app.DialogFragment
import androidx.preference.EditTextPreference
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import n7.ad2.databinding.DialogThemeBinding
import n7.ad2.ui.setting.domain.model.Theme

class DialogTheme : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val settingsFragment = parentFragment as SettingsFragment

        val dialogView = DialogThemeBinding.inflate(layoutInflater).apply {
            bDark.setOnClickListener { settingsFragment.applyTheme(Theme.PURPLE) }
            bGray.setOnClickListener { settingsFragment.applyTheme(Theme.RED) }
            bWhite.setOnClickListener { settingsFragment.applyTheme(Theme.BLUE) }
        }

        val dialogBuilder = MaterialAlertDialogBuilder(requireContext())
                .setView(dialogView.root)

        return dialogBuilder.create()
    }

}

class DialogThemePreference(context: Context, attrs: AttributeSet) : EditTextPreference(context, attrs)

