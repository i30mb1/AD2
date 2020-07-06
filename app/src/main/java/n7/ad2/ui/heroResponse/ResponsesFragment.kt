package n7.ad2.ui.heroResponse

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.observe
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import n7.ad2.R
import n7.ad2.databinding.FragmentHeroResponsesBinding
import n7.ad2.di.injector
import n7.ad2.ui.heroPage.AudioExoPlayer
import n7.ad2.ui.heroPage.HeroPageViewModel
import n7.ad2.utils.StickyHeaderDecorator
import n7.ad2.utils.viewModel
import java.io.File

class ResponsesFragment : Fragment(R.layout.fragment_hero_responses) {

    private lateinit var responsesPagedListAdapter: ResponsesListAdapter
    private lateinit var binding: FragmentHeroResponsesBinding
    private val viewModel by viewModel { injector.responsesViewModel }
    private val heroPageViewModel by activityViewModels<HeroPageViewModel>()
    private lateinit var audioExoPlayer: AudioExoPlayer

    companion object {
        fun newInstance(): ResponsesFragment = ResponsesFragment()
    }

    //    private int initialKey;
    var onComplete: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            responsesPagedListAdapter!!.notifyDataSetChanged()
            Snackbar.make(binding!!.root, R.string.responses_fragment_sound_downloaded, Snackbar.LENGTH_SHORT).setAction(R.string.open_file) {
                if (getContext() != null) {
                    val selectedUri = Uri.parse(requireActivity().getExternalFilesDir(Environment.DIRECTORY_RINGTONES).toString() + File.separator)
                    val intentOpenFile = Intent(Intent.ACTION_VIEW)
                    intentOpenFile.setDataAndType(selectedUri, "application/*")
                    if (intentOpenFile.resolveActivityInfo(requireActivity().packageManager, 0) != null) {
                        startActivity(Intent.createChooser(intentOpenFile, getString(R.string.all_open_folder)))
                    } else {
                        // if you reach this place, it means there is no any file
                        // explorer app installed on your device
                    }
                }
            }.show()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentHeroResponsesBinding.bind(view).also {
            it.lifecycleOwner = viewLifecycleOwner
        }
        audioExoPlayer = AudioExoPlayer(requireActivity().application, lifecycle)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        heroPageViewModel.hero.observe(viewLifecycleOwner, viewModel::loadResponses)
        setupPagedListAdapter()
    }

    private fun setupPagedListAdapter() {
        responsesPagedListAdapter = ResponsesListAdapter(audioExoPlayer)
        binding.rv.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            addItemDecoration(StickyHeaderDecorator(this, responsesPagedListAdapter))
            adapter = responsesPagedListAdapter
        }

        viewModel.voResponses.observe(viewLifecycleOwner) {
            responsesPagedListAdapter.submitList(it)
        }
    }
}