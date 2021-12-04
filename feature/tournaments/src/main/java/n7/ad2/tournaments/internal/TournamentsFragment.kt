package n7.ad2.tournaments.internal

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import n7.ad2.android.DrawerPercentListener
import n7.ad2.android.extension.viewModel
import n7.ad2.android.findDependencies
import n7.ad2.tournaments.R
import n7.ad2.tournaments.databinding.FragmentTournamentsBinding
import n7.ad2.tournaments.internal.adapter.TournamentsItemDecorator
import n7.ad2.tournaments.internal.di.DaggerTournamentsComponent
import javax.inject.Inject

class TournamentsFragment : Fragment(R.layout.fragment_tournaments) {

    companion object {
        fun getInstance() = TournamentsFragment()
    }

    @Inject lateinit var tournamentsViewModelFactory: TournamentsViewModel.Factory

    private var _binding: FragmentTournamentsBinding? = null
    private val binding: FragmentTournamentsBinding get() = _binding!!
    private val tournamentsItemDecorator = TournamentsItemDecorator()
    private val viewModel: TournamentsViewModel by viewModel { tournamentsViewModelFactory.create() }
//    private val onItemClick: (hero: VOItem.Body) -> Unit = { model ->
//        startItemInfoFragment(model)
//    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        DaggerTournamentsComponent.factory().create(findDependencies()).inject(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentTournamentsBinding.bind(view)
        setupAdapter()
        setupInsets()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun setupAdapter() {

    }

    private fun setupInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.rv) { _, insets ->
            val statusBarsInsets = insets.getInsets(WindowInsetsCompat.Type.statusBars())
            val navigationBarsInsets = insets.getInsets(WindowInsetsCompat.Type.navigationBars())
            tournamentsItemDecorator.statusBarsInsets = statusBarsInsets.top
            tournamentsItemDecorator.navigationBarsInsets = navigationBarsInsets.bottom
            insets
        }
        (parentFragment as DrawerPercentListener).setDrawerPercentListener { percent ->
            tournamentsItemDecorator.percent = percent
            binding.rv.invalidateItemDecorations()
        }
    }

    private fun startGamesWorker() {
//        val data = Data.Builder().putInt(TournamentsWorker.PAGE, 0).putBoolean(TournamentsWorker.DELETE_TABLE, true).build()
//        val worker = OneTimeWorkRequest.Builder(TournamentsWorker::class.java).setInputData(data).build()
//        WorkManager.getInstance(requireContext()).beginUniqueWork(TournamentsWorker.TAG, ExistingWorkPolicy.KEEP, worker).enqueue()
    }

}