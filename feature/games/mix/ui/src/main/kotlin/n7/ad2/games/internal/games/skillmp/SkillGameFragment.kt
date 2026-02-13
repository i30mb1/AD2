package n7.ad2.games.internal.games.skillmp

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.fragment.app.Fragment
import n7.ad2.android.findDependencies
import n7.ad2.games.internal.di.DaggerGamesComponent
import n7.ad2.games.internal.games.skillmp.compose.ManaPointScreen
import n7.ad2.ktx.viewModel
import n7.ad2.ui.content
import javax.inject.Inject

internal class SkillGameFragment : Fragment() {

    companion object {
        fun getInstance() = SkillGameFragment()
    }

    @Inject lateinit var skillGameViewModelFactory: SkillGameViewModel.Factory
    private val viewModel: SkillGameViewModel by viewModel { skillGameViewModelFactory.create() }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        DaggerGamesComponent.factory().create(findDependencies()).inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View = content {
        val state: State<SkillGameViewModel.State> = viewModel.state.collectAsState()
        ManaPointScreen(state = state.value, ::loadQuestion)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.onAction(SkillGameViewModel.Action.LoadQuestion)
    }

    private fun loadQuestion(selectedSpell: SkillGameViewModel.Spell) {
        viewModel.onAction(SkillGameViewModel.Action.ShowAnswer(selectedSpell))
    }
}
