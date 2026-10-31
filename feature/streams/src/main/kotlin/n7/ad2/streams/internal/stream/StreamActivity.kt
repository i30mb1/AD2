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
import android.view.WindowManager
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.content.getSystemService
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.media3.common.util.UnstableApi
import n7.ad2.android.findDependencies
import n7.ad2.core.ui.compose.AppTheme
import n7.ad2.ktx.viewModel
import n7.ad2.streams.internal.di.DaggerStreamsComponent
import n7.ad2.streams.internal.stream.compose.StreamScreen
import javax.inject.Inject

private const val VIDEO_WIDTH = 16
private const val VIDEO_HEIGHT = 9

/** Full screen landscape player, the screen does not go dark while it is open. */
@UnstableApi
internal class StreamActivity : FragmentActivity() {

    companion object {
        private const val STREAMER_NAME = "STREAMER_NAME"
        fun newInstance(context: Context, streamerName: String) = Intent(context, StreamActivity::class.java).apply {
            putExtra(STREAMER_NAME, streamerName)
        }
    }

    @Inject lateinit var streamFactory: StreamViewModel.Factory
    private var streamerName by mutableStateOf("")
    private val viewModel: StreamViewModel by viewModel { streamFactory.create() }
    private var isInPip by mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        DaggerStreamsComponent.factory().create(findDependencies()).inject(this)
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        hideSystemBars()
        streamerName = intent.extras?.getString(STREAMER_NAME)!!
        viewModel.load(streamerName)
        val isPipAvailable = hasPipPermission()
        setContent {
            AppTheme {
                val url by viewModel.url.collectAsStateWithLifecycle()
                val info by viewModel.info.collectAsStateWithLifecycle()
                val chatDensity by viewModel.chatDensity.collectAsStateWithLifecycle()
                StreamScreen(
                    streamerName = streamerName,
                    uri = url,
                    info = info,
                    chat = viewModel.chat,
                    chatDensity = chatDensity,
                    isInPip = isInPip,
                    isPipAvailable = isPipAvailable,
                    onChatDensityChanged = viewModel::setChatDensity,
                    onBackClicked = ::finish,
                    onPipClicked = ::onPipClicked,
                    onPipLayoutChanged = ::onPipLayoutChanged,
                )
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        streamerName = intent.extras?.getString(STREAMER_NAME) ?: return
        viewModel.load(streamerName)
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        // a dialog or the notification shade brings the bars back
        if (hasFocus) hideSystemBars()
    }

    private fun hideSystemBars() {
        WindowCompat.getInsetsController(window, window.decorView).apply {
            systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            hide(WindowInsetsCompat.Type.systemBars())
        }
    }

    private fun hasPipPermission(): Boolean {
        val opsManager = getSystemService<AppOpsManager>()
        val hasFeature = packageManager.hasSystemFeature(PackageManager.FEATURE_PICTURE_IN_PICTURE)
        val isAllowed = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            opsManager?.unsafeCheckOpNoThrow(AppOpsManager.OPSTR_PICTURE_IN_PICTURE, android.os.Process.myUid(), packageName) == AppOpsManager.MODE_ALLOWED
        } else {
            @Suppress("DEPRECATION")
            opsManager?.checkOpNoThrow(AppOpsManager.OPSTR_PICTURE_IN_PICTURE, android.os.Process.myUid(), packageName) == AppOpsManager.MODE_ALLOWED
        }
        return hasFeature && isAllowed
    }

    private fun onPipLayoutChanged(rect: Rect) {
        setPictureInPictureParams(
            PictureInPictureParams.Builder()
                .setSourceRectHint(rect)
                .setAspectRatio(Rational(VIDEO_WIDTH, VIDEO_HEIGHT))
                .build(),
        )
    }

    private fun onPipClicked() {
        val builder = PictureInPictureParams.Builder().setAspectRatio(Rational(VIDEO_WIDTH, VIDEO_HEIGHT))
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            builder.setSeamlessResizeEnabled(true).setAutoEnterEnabled(true)
        }
        enterPictureInPictureMode(builder.build())
    }

    override fun onPictureInPictureModeChanged(isInPictureInPictureMode: Boolean, newConfig: Configuration) {
        super.onPictureInPictureModeChanged(isInPictureInPictureMode, newConfig)
        isInPip = isInPictureInPictureMode
    }
}
