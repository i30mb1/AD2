package n7.ad2.settings.internal

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.play.core.review.ReviewManagerFactory
import n7.ad2.feature.settings.R
import n7.ad2.settings.internal.compose.SettingsScreen
import n7.ad2.ui.ComposeView

class SettingsFragment : Fragment() {

    companion object {
        fun getInstance(): SettingsFragment = SettingsFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return ComposeView {
            SettingsScreen(
                ::onReviewAppButtonClicked,
                ::onTellFriendsButtonClicked,
            )
        }
    }

    private fun onReviewAppButtonClicked() {
        val reviewManager = ReviewManagerFactory.create(requireContext())
        reviewManager.requestReviewFlow().addOnCompleteListener { request ->
            if (request.isSuccessful) {
                val reviewInfo = request.result ?: return@addOnCompleteListener
                val flow = reviewManager.launchReviewFlow(requireActivity(), reviewInfo)
                flow.addOnCompleteListener {

                }
            }
        }
    }

    private fun onTellFriendsButtonClicked() {
        Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, getString(R.string.setting_tell_friends_subject))
            putExtra(Intent.EXTRA_TEXT, getString(R.string.setting_tell_friends_text))
            startActivity(Intent.createChooser(this, getString(R.string.setting_tell_friend_title)))
        }
    }

    private fun setupContact() {
        StringBuilder().apply {
            append("\n\n\n--------------------------\n")
//            append("App Version: ").append(getString(R.string.setting_about_summary, BuildConfig.VERSION_NAME)).append("\n")
            append("Device Brand: ").append(Build.BRAND).append("\n")
            append("Device Model: ").append(Build.MODEL).append("\n")
            append("OS: Android ").append(Build.VERSION.RELEASE).append("(").append(Build.VERSION.CODENAME).append(") \n")

            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse("mailto:fate.i30mb1@gmail.com?subject=Feedback about AD2 on Android&body=hehe")
            startActivity(intent)
        }
    }

}