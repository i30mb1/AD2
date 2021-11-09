package n7.ad2.news

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import n7.ad2.R
import n7.ad2.databinding.FragmentNewsBinding
import n7.ad2.di.injector
import n7.ad2.utils.viewModel

class NewsFragment : Fragment(R.layout.fragment_news) {

    companion object {
        fun getInstance(): NewsFragment = NewsFragment()
    }

    private var _binding: FragmentNewsBinding? = null
    private val binding: FragmentNewsBinding get() = _binding!!
    private val viewModel: NewsViewModel by viewModel { injector.newsViewModel }

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
        val newsPagedListAdapter = NewsPagedListAdapter(true)
        binding.rvNews.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = newsPagedListAdapter
        }
        viewModel.news?.observe(viewLifecycleOwner, newsPagedListAdapter::submitList)
    }
}