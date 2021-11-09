package n7.ad2.games

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import n7.ad2.R
import n7.ad2.databinding.FragmentGameBinding

class GameFragment : Fragment(R.layout.fragment_game) {

    companion object {
        fun getInstance() = GameFragment()
    }

    private var _binding: FragmentGameBinding? = null
    private val binding: FragmentGameBinding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentGameBinding.bind(view)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun startG1P1() {
        val intent = Intent(context, Game1p1::class.java)
        startActivity(intent)
    }

    fun startG1P2() {
        val intent = Intent(context, Game1p2::class.java)
        startActivity(intent)
    }

    fun startG2P1() {
        val intent = Intent(context, Game2p1::class.java)
        startActivity(intent)
    }

    fun startG3P1() {
        val intent = Intent(context, Game3p1::class.java)
        startActivity(intent)
    }

    fun startG2P2() {
        val intent = Intent(context, Game2p2::class.java)
        startActivity(intent)
    }


}