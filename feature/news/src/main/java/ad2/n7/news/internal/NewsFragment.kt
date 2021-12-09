package ad2.n7.news.internal

import ad2.n7.news.R
import ad2.n7.news.databinding.FragmentNewsBinding
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import n7.ad2.ktx.viewModel
import javax.inject.Inject

internal class NewsFragment : Fragment(R.layout.fragment_news) {

    companion object {
        fun getInstance(): NewsFragment = NewsFragment()
    }

    @Inject lateinit var newsViewModelFactory: NewsViewModel.Factory

    private var _binding: FragmentNewsBinding? = null
    private val binding: FragmentNewsBinding get() = _binding!!
    private val viewModel: NewsViewModel by viewModel { newsViewModelFactory.create() }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentNewsBinding.bind(view)
        setupRecyclerView()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupRecyclerView() {
//        val newsPagedListAdapter = NewsPagedListAdapter(true)
//        binding.rvNews.apply {
//            layoutManager = LinearLayoutManager(context)
//            adapter = newsPagedListAdapter
//        }
//        viewModel.news?.observe(viewLifecycleOwner, newsPagedListAdapter::submitList)
    }
}