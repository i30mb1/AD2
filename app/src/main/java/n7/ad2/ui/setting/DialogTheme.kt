package n7.ad2.ui.setting

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import n7.ad2.R
import n7.ad2.databinding.DialogSettingFragmentThemeBinding

class DialogTheme : DialogFragment() {

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
//        dialog?.window?.setWindowAnimations(R.style.MyWindowAnimationTransition)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialogView = DialogSettingFragmentThemeBinding.inflate(layoutInflater)

        val dialogBuilder = MaterialAlertDialogBuilder(context)
                .setView(dialogView.root)


        return dialogBuilder.create()
    }

}