package n7.ad2.hero_page.internal.pager

import android.app.Dialog
import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import n7.ad2.hero_page.databinding.DialogErrorBinding

fun Fragment.showDialogError(throwable: Throwable) = showDialogError(throwable.toString())

fun Fragment.showDialogError(title: String) {
    val dialogError = DialogError.newInstance(title)
    dialogError.show(childFragmentManager, null)
}

fun FragmentActivity.showDialogError(throwable: Throwable) = showDialogError(throwable.toString())

fun FragmentActivity.showDialogError(title: String) {
    val dialogError = DialogError.newInstance(title)
    dialogError.show(supportFragmentManager, null)
}

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
            body.text = requireArguments().getString(BODY_KEY)
        }

        val dialogBuilder = MaterialAlertDialogBuilder(requireContext())
            .setView(dialogView.root)

        return dialogBuilder.create()
    }

}