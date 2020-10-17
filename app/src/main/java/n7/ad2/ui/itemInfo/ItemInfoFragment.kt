package n7.ad2.ui.itemInfo

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import n7.ad2.R
import n7.ad2.databinding.FragmentItemsBinding
import n7.ad2.ui.heroInfo.DescriptionsListAdapter
import n7.ad2.ui.heroInfo.InfoPopupWindow
import n7.ad2.ui.heroPage.AudioExoPlayer
import n7.ad2.ui.heroPage.showDialogError

class ItemInfoFragment : Fragment(R.layout.fragment_item_info) {

    private lateinit var binding: FragmentItemsBinding
    lateinit var audioExoPlayer: AudioExoPlayer
    private lateinit var infoPopupWindow: InfoPopupWindow

    companion object {
        fun newInstance(): ItemInfoFragment = ItemInfoFragment()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentItemsBinding.bind(view).also {
            it.lifecycleOwner = viewLifecycleOwner
        }

        setupAudioPlayer()
        setupItemInfoRecyclerView()
    }

    private fun setupAudioPlayer() {
        audioExoPlayer = AudioExoPlayer(requireActivity().application, lifecycle)
        audioExoPlayer.setErrorListener(::showDialogError)
    }

    private fun setupItemInfoRecyclerView() {
        val adapter = DescriptionsListAdapter(audioExoPlayer, infoPopupWindow)
        binding.rv.adapter = adapter
    }

}