package n7.ad2.news

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import n7.ad2.R
import n7.ad2.databinding.FragmentNewsBinding
import n7.ad2.di.injector
import n7.ad2.utils.viewModel

class NewsFragment : Fragment() {

    private var _binding: FragmentNewsBinding? = null
    private val binding: FragmentNewsBinding get() = _binding!!
    private val viewModel: NewsViewModel by viewModel { injector.newsViewModel }

    companion object {
        fun newInstance(): NewsFragment = NewsFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = DataBindingUtil.inflate(inflater, R.layout.fragment_news, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewModel = viewModel
        requireActivity().setTitle(R.string.news)
        setupRecyclerView()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupRecyclerView() {
        val gridLayoutManager = GridLayoutManager(context, 1)
        binding.rvFragmentNews.layoutManager = gridLayoutManager
        val adapter = NewsPagedListAdapter(true)
        binding.rvFragmentNews.adapter = adapter
        viewModel.news?.observe(viewLifecycleOwner, { newsModels -> adapter.submitList(newsModels) })
    }
}