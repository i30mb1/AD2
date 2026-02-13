package n7.ad2.hero.page.internal.responses

import android.app.Dialog
import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import n7.ad2.feature.hero.page.ui.databinding.DialogResponseBinding

class DialogResponse : DialogFragment() {

    companion object {
        const val REQUEST_KEY = "REQUEST_KEY"
        const val RESULT_KEY = "RESULT_KEY"
        const val ACTION_DOWNLOAD_RESPONSE = "ACTION_DOWNLOAD_RESPONSE"
        fun newInstance() = DialogResponse().apply {
            arguments = bundleOf()
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialogView = DialogResponseBinding.inflate(layoutInflater).apply {
            bDownload.setOnClickListener {
                setFragmentResult(REQUEST_KEY, bundleOf(RESULT_KEY to ACTION_DOWNLOAD_RESPONSE))
                dismiss()
            }
        }
        val dialogBuilder = MaterialAlertDialogBuilder(requireContext()).setView(dialogView.root)

        return dialogBuilder.create()
    }
}
