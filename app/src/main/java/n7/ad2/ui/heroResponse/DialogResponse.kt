package n7.ad2.ui.heroResponse

import android.app.Dialog
import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import n7.ad2.databinding.DialogResponseBinding

class DialogResponse : DialogFragment() {

    companion object {
        const val DOWNLOAD_ID_KEY = "DOWNLOAD_ID_KEY"
        fun newInstance(id: Long) = DialogResponse().apply {
            arguments = bundleOf(DOWNLOAD_ID_KEY to id)
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialogView = DialogResponseBinding.inflate(layoutInflater).apply {

        }
        val dialogBuilder = MaterialAlertDialogBuilder(requireContext())
                .setView(dialogView.root)

        return dialogBuilder.create()
    }

}