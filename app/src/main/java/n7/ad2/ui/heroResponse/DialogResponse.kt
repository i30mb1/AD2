package n7.ad2.ui.heroResponse

import android.app.Dialog
import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.setFragmentResultListener
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import n7.ad2.databinding.DialogResponseBinding

class DialogResponse : DialogFragment() {

    companion object {
        const val REQUEST_KEY = "REQUEST_KEY"
        const val DOWNLOAD_ID_KEY = "DOWNLOAD_ID_KEY"
        fun newInstance(id: Long = 0) = DialogResponse().apply {
            arguments = bundleOf(DOWNLOAD_ID_KEY to id)
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialogView = DialogResponseBinding.inflate(layoutInflater).apply {
            bDownload.setOnClickListener {
                setFragmentResult(REQUEST_KEY, bundleOf(DOWNLOAD_ID_KEY to "OK"))
                dismiss()
            }
        }
        val dialogBuilder = MaterialAlertDialogBuilder(requireContext())
                .setView(dialogView.root)

        return dialogBuilder.create()
    }

}