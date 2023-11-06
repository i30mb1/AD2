package n7.ad2.games.internal.games.killCreep

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import n7.ad2.android.findDependencies
import n7.ad2.games.internal.di.DaggerGamesComponent
import n7.ad2.games.internal.games.killCreep.compose.KillCreepScreen
import n7.ad2.ui.ComposeView

class KillCreepFragment : Fragment() {

    companion object {
        fun getInstance() = KillCreepFragment()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        DaggerGamesComponent.factory().create(findDependencies()).inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return ComposeView {
            KillCreepScreen()
        }
    }

}