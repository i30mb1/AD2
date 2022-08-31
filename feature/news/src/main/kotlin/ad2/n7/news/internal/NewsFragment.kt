package ad2.n7.news.internal

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.material.Text
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import n7.ad2.ktx.viewModel
import javax.inject.Inject

internal class NewsFragment : Fragment() {

    companion object {
        fun getInstance(): NewsFragment = NewsFragment()
    }

    @Inject lateinit var newsViewModelFactory: NewsViewModel.Factory
    private lateinit var root: ComposeView

    private val viewModel: NewsViewModel by viewModel { newsViewModelFactory.create() }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        root = ComposeView(requireContext())
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        root.setContent {
            Text(text = "hello")
        }
    }

}