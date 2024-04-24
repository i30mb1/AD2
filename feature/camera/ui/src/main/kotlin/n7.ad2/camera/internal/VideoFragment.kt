package n7.ad2.camera.internal

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.MediaController
import android.widget.VideoView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment

internal class VideoFragment : Fragment() {

    private lateinit var videoView: VideoView
    private val videoUri: Uri by lazy { requireArguments().getParcelable(URI)!! }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        videoView = VideoView(context).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT,
            )
        }
        return videoView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        videoView.setVideoURI(videoUri)
        videoView.start()

        val mediaController = MediaController(context)
        videoView.setMediaController(mediaController)
        mediaController.setAnchorView(videoView.rootView)
    }

    companion object {
        private const val URI = "URI"
        fun newInstance(videoUri: Uri) = VideoFragment().apply {
            arguments = bundleOf(URI to videoUri)
        }
    }

}