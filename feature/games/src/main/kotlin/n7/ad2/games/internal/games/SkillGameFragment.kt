package n7.ad2.games.internal.games

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.fragment.app.Fragment
import n7.ad2.android.extension.viewModel
import n7.ad2.android.findDependencies
import n7.ad2.games.internal.di.DaggerGamesComponent
import n7.ad2.games.internal.games.skillmp.ManaPointScreen
import n7.ad2.games.internal.games.skillmp.SkillGameViewModel
import n7.ad2.ui.ComposeView
import javax.inject.Inject

class SkillGameFragment : Fragment() {

    companion object {
        fun getInstance(): SkillGameFragment = SkillGameFragment()
    }

    @Inject lateinit var skillGameViewModelFactory: SkillGameViewModel.Factory
    private val viewModel: SkillGameViewModel by viewModel { skillGameViewModelFactory.create() }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        DaggerGamesComponent.factory().create(findDependencies()).inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return ComposeView {
            val state: State<SkillGameViewModel.State> = viewModel.state.collectAsState()
            ManaPointScreen(state = state.value, viewModel::loadQuestion)
        }
    }

}

//    private fun setEndText() {
//        when (score.get()) {
//            -6 -> endText.set("SUCK MY DATA, HUMAN!")
//            -5 -> endText.set("If our lives are already written, it would take a courageous man to change the script.")
//            -4 -> endText.set("Stand in the ashes of a trillion dead souls and ask the ghosts if honor matters. Their silence is your answer.")
//            -3 -> endText.set("You are great in bed ;) …. You can sleep for days.")
//            -2 -> endText.set("You didn’t fall. The floor just needed a hug.")
//            -1 -> endText.set("SKELETON_KING PLAYER DETECTED.")
//            0, 1 -> endText.set("GG&WP!")
//            2 -> endText.set("Are you a boy or a girl?")
//            3 -> {
//                endText.set("sorry, you lose!")
//                endText.set("you do not get older. you lvl up.")
//            }
//            4 -> endText.set("you do not get older. you lvl up.")
//            5 -> {
//                endText.set("DOES THIS UNIT HAVE A SOUL?")
//                soundPool.play(does_this_unit_have_a_soul, 0.6f, 0.6f, 0, 0, 1f)
//            }
//            6 -> endText.set("A hero need not speak. When he is gone, the world will speak for him.")
//            7 -> {
//                endText.set("UGANDA FOREVER!")
//                soundPool.play(you_are_the_most_successful, 0.6f, 0.6f, 0, 0, 1f)
//            }
//            8 -> endText.set("You are nobody.. nobody's perfect.. therefore YOU ARE PERFECT!")
//            9 -> endText.set("If anything’s possible, then is it possible that nothing’s possible?")
//            10 -> {
//                endText.set("YOU SUCCEEDED WHERE OTHER DO NOT.")
//                soundPool.play(you_are_succeeded_where_other_did_not, 0.6f, 0.6f, 0, 0, 1f)
//            }
//            else -> endText.set("GG&WP!")
//        }
//    }