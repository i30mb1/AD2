package n7.ad2.hero.page.demo

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.commitNow
import n7.ad2.hero.page.api.HeroPageProvider

internal class HeroPageActivityDemo : FragmentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        // On a configuration change the FragmentManager restores the pager fragment into the
        // stable android.R.id.content container itself, so we add it only on the first creation.
        // Previously setContentView + add were both inside this guard, so after a recreation the
        // activity had no content view at all and rendered an empty screen.
        if (savedInstanceState == null) {
            supportFragmentManager.commitNow {
                add(android.R.id.content, HeroPageProvider().getPagerFragment(HERO_NAME), null)
            }
        }
    }

    private companion object {
        const val HERO_NAME = "abaddon"
    }
}
