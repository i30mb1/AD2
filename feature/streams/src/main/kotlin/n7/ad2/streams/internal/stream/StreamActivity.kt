package n7.ad2.streams.internal.stream

import android.app.PictureInPictureParams
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Rect
import android.os.Build
import android.os.Bundle
import android.util.Rational
import androidx.activity.compose.setContent
import androidx.compose.runtime.collectAsState
import androidx.fragment.app.FragmentActivity
import androidx.media3.common.util.UnstableApi
import n7.ad2.android.findDependencies
import n7.ad2.ktx.lazyUnsafe
import n7.ad2.ktx.viewModel
import n7.ad2.streams.internal.di.DaggerStreamsComponent
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

    override fun onCreate(savedInstanceState: Bundle?) {
        DaggerStreamsComponent.factory().create(findDependencies()).inject(this)
        super.onCreate(savedInstanceState)
        viewModel.load(streamerName)
        setContent {
            AppTheme {
                val url = viewModel.url.collectAsState()
                StreamScreen(url.value, ::onPipClicked, ::onPipLayoutChanged)
            }
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        val streamerName = intent?.extras?.getString(STREAMER_NAME) ?: return
        viewModel.load(streamerName)
    }

    private fun onPipLayoutChanged(rect: Rect) {
        setPictureInPictureParams(
            PictureInPictureParams.Builder()
                .setSourceRectHint(rect)
                .build()
        )
    }

    private fun onPipClicked() {
        val builder = PictureInPictureParams.Builder()
            .setAspectRatio(Rational(16, 9))
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            builder.setSeamlessResizeEnabled(true).setAutoEnterEnabled(true)
        }
        enterPictureInPictureMode(builder.build())
    }

    override fun onPictureInPictureModeChanged(isInPictureInPictureMode: Boolean, newConfig: Configuration) {
        super.onPictureInPictureModeChanged(isInPictureInPictureMode, newConfig)

    }

}