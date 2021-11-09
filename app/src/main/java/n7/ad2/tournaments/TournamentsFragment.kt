package n7.ad2.tournaments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import n7.ad2.R
import n7.ad2.databinding.FragmentTournamentsBinding
import n7.ad2.di.injector
import n7.ad2.utils.viewModel

class TournamentsFragment : Fragment(R.layout.fragment_tournaments) {

    companion object {
        fun getInstance() = TournamentsFragment()
    }

    private var _binding: FragmentTournamentsBinding? = null
    private val binding: FragmentTournamentsBinding get() = _binding!!
    private val viewModel: TournamentsViewModel by viewModel { injector.tournamentsViewModel }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentTournamentsBinding.bind(view)
        setupAdapter()
        startGamesWorker()
    }

    private fun startGamesWorker() {
        val data = Data.Builder().putInt(TournamentsWorker.PAGE, 0).putBoolean(TournamentsWorker.DELETE_TABLE, true).build()
        val worker = OneTimeWorkRequest.Builder(TournamentsWorker::class.java).setInputData(data).build()
        WorkManager.getInstance(requireContext()).beginUniqueWork(TournamentsWorker.TAG, ExistingWorkPolicy.KEEP, worker).enqueue()
    }

    private fun setupAdapter() {
        val linearLayoutManager = LinearLayoutManager(context)
        val tournamentsPagedListAdapter = TournamentsPagedListAdapter(viewLifecycleOwner)
        binding.rvTournaments.apply {
            layoutManager = linearLayoutManager
            adapter = tournamentsPagedListAdapter
            setHasFixedSize(true)
        }
        viewModel.tournamentsGames.observe(viewLifecycleOwner, tournamentsPagedListAdapter::submitList)
    }
}