package ad2.n7.settings.internal

import ad2.n7.settings.R
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import com.google.android.play.core.review.ReviewManagerFactory
import n7.ad2.ui.ComposeView
import n7.ad2.ui.compose.AppTheme

class SettingsFragment : Fragment() {

    companion object {
        fun getInstance(): SettingsFragment = SettingsFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
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

    @Preview
    @Composable
    fun SettingsScreen(
        onAppReviewButtonClicked: () -> Unit = {},
        onTellFriendsButtonClicked: () -> Unit = {},
    ) {
        Column {
            SimpleItem(stringResource(R.string.setting_review_app), onAppReviewButtonClicked)
            SimpleItem(stringResource(R.string.setting_tell_friend_about_this_app), onTellFriendsButtonClicked)
        }
    }

    @Composable
    fun SimpleItem(
        name: String,
        onSimpleItemClicked: () -> Unit,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(AppTheme.color.surface)
                .clickable { onSimpleItemClicked() }
                .padding(12.dp, 8.dp),
        ) {
            Text(
                text = name,
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .weight(1f),
                color = AppTheme.color.textColor
            )
            Icon(
                Icons.Default.Star, null
            )
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