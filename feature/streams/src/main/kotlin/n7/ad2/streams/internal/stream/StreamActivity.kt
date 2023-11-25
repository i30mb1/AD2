package n7.ad2.streams.internal.stream

import android.app.AppOpsManager
import android.app.PictureInPictureParams
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.Rect
import android.os.Build
import android.os.Bundle
import android.util.Rational
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.core.content.getSystemService
import androidx.core.view.WindowCompat
import androidx.fragment.app.FragmentActivity
import androidx.media3.common.util.UnstableApi
import n7.ad2.android.findDependencies
import n7.ad2.ktx.lazyUnsafe
import n7.ad2.ktx.viewModel
import n7.ad2.streams.internal.di.DaggerStreamsComponent
import n7.ad2.streams.internal.stream.compose.StreamScreen
import n7.ad2.ui.compose.AppTheme
import javax.inject.Inject

@UnstableApi
class StreamActivity : FragmentActivity() {

    companion object {
        private const val STREAMER_NAME = "STREAMER_NAME"
        fun newInstance(
            context: Context,
            streamerName: String,
        ) = Intent(context, StreamActivity::class.java).apply {
            putExtra(STREAMER_NAME, streamerName)
        }
    }

    @Inject lateinit var streamFactory: StreamViewModel.Factory
    private val streamerName by lazyUnsafe { intent.extras?.getString(STREAMER_NAME)!! }
    private val viewModel: StreamViewModel by viewModel { streamFactory.create() }
    private var isPipVisible by mutableStateOf(true)

    override fun onCreate(savedInstanceState: Bundle?) {
        DaggerStreamsComponent.factory().create(findDependencies()).inject(this)
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        viewModel.load(streamerName)
        setContent {
            AppTheme {
                val url = viewModel.url.collectAsState()
                val opsManager = remember { getSystemService<AppOpsManager>() }
                val hasPipFeature = remember {
                    val hasFeature = packageManager.hasSystemFeature(PackageManager.FEATURE_PICTURE_IN_PICTURE)
                    val isAllowed = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        opsManager?.unsafeCheckOpNoThrow(AppOpsManager.OPSTR_PICTURE_IN_PICTURE, android.os.Process.myUid(), packageName) == AppOpsManager.MODE_ALLOWED
                    } else {
                        opsManager?.checkOpNoThrow(AppOpsManager.OPSTR_PICTURE_IN_PICTURE, android.os.Process.myUid(), packageName) == AppOpsManager.MODE_ALLOWED
                    }
                    hasFeature && isAllowed
                }
                val hasPip = hasPipFeature && isPipVisible
                StreamScreen(url.value, hasPip, ::onPipClicked, ::onPipLayoutChanged)
            }
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        val streamerName = intent?.extras?.getString(STREAMER_NAME) ?: return
        viewModel.load(streamerName)
    }

    private fun onSettingsClicked() {

    }

    private fun onPipLayoutChanged(rect: Rect) {
        setPictureInPictureParams(
            PictureInPictureParams.Builder()
                .setSourceRectHint(rect)
                .setAspectRatio(Rational(rect.right, rect.bottom))
                .build()
        )
    }

    private fun onPipClicked() {
        val builder = PictureInPictureParams.Builder()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            builder.setSeamlessResizeEnabled(true).setAutoEnterEnabled(true)
        }
        enterPictureInPictureMode(builder.build())
    }

    override fun onPictureInPictureModeChanged(isInPictureInPictureMode: Boolean, newConfig: Configuration) {
        super.onPictureInPictureModeChanged(isInPictureInPictureMode, newConfig)
        isPipVisible = !isInPictureInPictureMode
    }

}
