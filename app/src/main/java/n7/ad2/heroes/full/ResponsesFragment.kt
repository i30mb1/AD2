package n7.ad2.heroes.full

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import n7.ad2.R
import n7.ad2.databinding.FragmentHeroResponsesBinding
import n7.ad2.ui.heroInfo.HeroInfoViewModel
import n7.ad2.ui.heroInfo.ResponsesListAdapter
import n7.ad2.utils.StickyHeaderDecorator
import java.io.File

class ResponsesFragment : Fragment(R.layout.fragment_hero_responses), SearchView.OnQueryTextListener {
    private lateinit var responsesPagedListAdapter: ResponsesListAdapter
    private var currentLanguage: String? = null
    private lateinit var binding: FragmentHeroResponsesBinding
    private var viewModel: HeroInfoViewModel? = null

    //    private int initialKey;
    var onComplete: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            viewModel!!.loadAvailableResponsesInMemory()
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
    private var linearLayoutManager: LinearLayoutManager? = null
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_fragment_hero_responses, menu)
        val searchHero = menu.findItem(R.id.action_search)
        val searchView = searchHero.actionView as SearchView
        searchView.setOnQueryTextListener(this)
    }

    fun switchLanguage(): String {
        currentLanguage = when (currentLanguage) {
            "ru" -> "eng"
            "eng" -> "ru"
            else -> "ru"
        }
        return currentLanguage!!
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_switch_language -> {
                viewModel!!.loadResponses(switchLanguage())
                responses
                item.title = currentLanguage
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentHeroResponsesBinding.bind(view).also {
            it.lifecycleOwner = viewLifecycleOwner
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(requireActivity()).get(HeroInfoViewModel::class.java)
        //        if (savedInstanceState == null) {
//            initialKey = 0;
//        } else {
//            initialKey = savedInstanceState.getInt("initialKey");
//        }
        currentLanguage = getString(R.string.language_resource)
        setHasOptionsMenu(true)
        setupPagedListAdapter()
        //        MyUtils.startSpriteAnim(getContext(), (ImageView) view.findViewById(R.id.iv_player2_heartRed2), "hero_responses_sprite.webp", true, 232, 232, 1000, 1);
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt("initialKey", linearLayoutManager!!.findFirstCompletelyVisibleItemPosition())
    }

    override fun onResume() {
        super.onResume()
        if (context != null) {
//            context!!.registerReceiver(onComplete, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))
        }
    }

    override fun onPause() {
        super.onPause()
        if (context != null) {
//            context!!.unregisterReceiver(onComplete)
        }
    }

    private fun setupPagedListAdapter() {
        responsesPagedListAdapter = ResponsesListAdapter(viewModel!!)
        binding.rv.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            addItemDecoration(StickyHeaderDecorator(this, responsesPagedListAdapter))
            adapter = responsesPagedListAdapter
        }

        viewModel.getResponsesPagedList("").observe(getViewLifecycleOwner(), new Observer<PagedList<Response>>() {
            @Override
            public void onChanged(@Nullable PagedList<Response> responses) {
                responsesPagedListAdapter.submitList(responses);
            }
        });
    }


    private val responses: Unit
        private get() {
//        viewModel.getResponsesPagedList("").observe(getViewLifecycleOwner(), new Observer<PagedList<Response>>() {
//            @Override
//            public void onChanged(@Nullable PagedList<Response> responses) {
//                responsesPagedListAdapter.submitList(responses);
//            }
//        });
        }

    override fun onQueryTextSubmit(query: String): Boolean {
        return false
    }

    override fun onQueryTextChange(newText: String): Boolean {
//        viewModel.getResponsesPagedList(newText).observe(this, new Observer<PagedList<Response>>() {
//            @Override
//            public void onChanged(@Nullable PagedList<Response> responses) {
//                responsesPagedListAdapter.submitList(responses);
//            }
//        });
        return true
    }

    companion object {
        fun newInstance(): ResponsesFragment {
            return ResponsesFragment()
        }
    }
}