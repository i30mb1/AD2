package n7.ad2.ui.heroPage

import android.app.Dialog
import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResultListener
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import n7.ad2.databinding.DialogErrorBinding
import n7.ad2.ui.heroResponse.ResponsesFragment

class DialogError : DialogFragment() {

    companion object {
        const val REQUEST_KEY = "REQUEST_KEY"
        const val BODY_KEY = "BODY_KEY"
        fun newInstance(title: String): DialogError = DialogError().apply {
            arguments = bundleOf(BODY_KEY to title)
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialogView = DialogErrorBinding.inflate(layoutInflater).apply {
            body.text = arguments?.getString(BODY_KEY)
        }

        val dialogBuilder = MaterialAlertDialogBuilder(requireContext())
                .setView(dialogView.root)

        return dialogBuilder.create()
    }

}