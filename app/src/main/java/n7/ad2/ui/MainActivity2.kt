package n7.ad2.ui

import android.os.Bundle
import androidx.fragment.app.commit
import n7.ad2.databinding.ActivityMain2Binding
import n7.ad2.ui.heroes.HeroesFragment
import n7.ad2.utils.BaseActivity

class MainActivity2 : BaseActivity() {

    private lateinit var binding: ActivityMain2Binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMain2Binding.inflate(layoutInflater)
        setContentView(binding.root)
        supportFragmentManager.commit {
            replace(binding.container.id, HeroesFragment())
        }
    }

}